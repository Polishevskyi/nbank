#!/bin/bash

# Full API Testing Pipeline with Quality Gate
# Runs locally the same steps as CI/CD

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}🚀 Full API Testing Pipeline with Quality Gate${NC}"
echo "=============================================="

# Configuration
START_TIME=$(date +%s)
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Step 1: Clean previous results
echo -e "${BLUE}🧹 Step 1: Cleaning previous results...${NC}"
rm -rf target/allure-results/*
rm -rf target/swagger-coverage-output/*
rm -f swagger-coverage-*.html
rm -f swagger-coverage-*.json
echo "✅ Cleanup completed"

# Step 2: Build application
echo -e "${BLUE}🏗️ Step 2: Building application...${NC}"
./mvnw clean compile -q
echo "✅ Build completed"

# Step 3: Run API tests
echo -e "${BLUE}🧪 Step 3: Running API tests...${NC}"
echo "Profile: api"
./mvnw test -Papi
echo "✅ API tests completed"

# Step 4: Generate Swagger Coverage
echo -e "${BLUE}📊 Step 4: Generating Swagger Coverage...${NC}"
if [ ! -d ".swagger-coverage-commandline" ]; then
    echo "❌ Swagger coverage tool not found!"
    echo "💡 Please download it first:"
    echo "   wget https://github.com/viclovsky/swagger-coverage/releases/download/1.5.0/swagger-coverage-commandline-1.5.0.zip"
    echo "   unzip swagger-coverage-commandline-1.5.0.zip"
    exit 1
fi

.swagger-coverage-commandline/bin/swagger-coverage-commandline \
  -s http://localhost:4111/v3/api-docs \
  -i target/swagger-coverage-output

echo "✅ Swagger coverage generated"

# Step 5: API Coverage Quality Gate
echo -e "${BLUE}🚪 Step 5: Running Quality Gate Check...${NC}"
if ./check-api-coverage.sh; then
    QUALITY_GATE_STATUS="PASSED"
    echo -e "${GREEN}✅ Quality Gate: PASSED${NC}"
else
    QUALITY_GATE_STATUS="FAILED"
    echo -e "${RED}❌ Quality Gate: FAILED${NC}"
fi

# Step 6: Integrate with Allure
echo -e "${BLUE}📋 Step 6: Integrating API Coverage with Allure...${NC}"
./integrate-api-coverage.sh
echo "✅ API Coverage integrated with Allure"

# Step 7: Generate Allure Report
echo -e "${BLUE}📋 Step 7: Generating Allure Report...${NC}"
if command -v allure &> /dev/null; then
    allure generate target/allure-results --clean -o allure-report
    echo "✅ Allure report generated"
    
    # Optional: Open report
    read -p "🌐 Open Allure report in browser? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        allure open allure-report
    fi
else
    echo "⚠️ Allure not installed - skipping report generation"
    echo "💡 Install with: brew install allure (macOS) or download from https://github.com/allure-framework/allure2/releases"
fi

# Step 8: Summary
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo ""
echo -e "${CYAN}📊 Pipeline Summary${NC}"
echo "==================="
echo -e "🕐 ${YELLOW}Duration:${NC} ${DURATION}s"
echo -e "🎯 ${YELLOW}Quality Gate:${NC} $QUALITY_GATE_STATUS"

# Read coverage metrics
if [ -f "swagger-coverage-results.json" ]; then
    TOTAL_CONDITIONS=$(jq -r '.conditionCounter.all' swagger-coverage-results.json)
    COVERED_CONDITIONS=$(jq -r '.conditionCounter.covered' swagger-coverage-results.json)
    COVERAGE_PERCENTAGE=$(echo "scale=1; $COVERED_CONDITIONS * 100 / $TOTAL_CONDITIONS" | bc)
    
    echo -e "📈 ${YELLOW}API Coverage:${NC} $COVERED_CONDITIONS/$TOTAL_CONDITIONS (${COVERAGE_PERCENTAGE}%)"
    
    FULL_ENDPOINTS=$(jq -r '.coverageOperationMap.counter.full' swagger-coverage-results.json)
    TOTAL_ENDPOINTS=$(jq -r '.coverageOperationMap.counter.all' swagger-coverage-results.json)
    echo -e "🏆 ${YELLOW}Full Coverage Endpoints:${NC} $FULL_ENDPOINTS/$TOTAL_ENDPOINTS"
fi

echo ""
echo -e "${CYAN}📁 Generated Artifacts:${NC}"
echo "- 📋 allure-report/ - Interactive Allure report"
echo "- 📊 swagger-coverage-report.html - Detailed API coverage"
echo "- 📄 swagger-coverage-results.json - Coverage metrics"
echo "- 🗂️ target/allure-results/ - Raw test results"

# Final status
if [ "$QUALITY_GATE_STATUS" = "PASSED" ]; then
    echo ""
    echo -e "${GREEN}🎉 PIPELINE SUCCESS!${NC}"
    echo -e "${GREEN}✅ All checks passed - ready for deployment${NC}"
    exit 0
else
    echo ""
    echo -e "${RED}🚫 PIPELINE FAILED!${NC}" 
    echo -e "${RED}❌ Quality gate failed - improve API coverage${NC}"
    exit 1
fi