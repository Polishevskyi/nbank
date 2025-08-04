#!/bin/bash

# API Coverage Quality Gate Script
# Fails build if API coverage < 50%

set -e

# Configuration
SWAGGER_COVERAGE_FILE="swagger-coverage-results.json"
MIN_COVERAGE_THRESHOLD=50
ALLURE_ENV_FILE="target/allure-results/environment.properties"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔍 API Coverage Quality Gate${NC}"
echo "=============================="

# Check if swagger coverage results exist
if [[ ! -f "$SWAGGER_COVERAGE_FILE" ]]; then
    echo -e "${RED}❌ Error: $SWAGGER_COVERAGE_FILE not found!${NC}"
    echo -e "${YELLOW}💡 Make sure to run swagger coverage analysis first:${NC}"
    echo "   .swagger-coverage-commandline/bin/swagger-coverage-commandline -s http://localhost:4111/v3/api-docs -i target/swagger-coverage-output"
    exit 1
fi

# Check if jq is available
if ! command -v jq &> /dev/null; then
    echo -e "${RED}❌ Error: jq is not installed!${NC}"
    echo -e "${YELLOW}💡 Install jq with: brew install jq (macOS) or apt-get install jq (Ubuntu)${NC}"
    exit 1
fi

# Extract coverage metrics using jq
echo -e "${BLUE}📊 Extracting API coverage metrics...${NC}"

TOTAL_CONDITIONS=$(jq -r '.conditionCounter.all' "$SWAGGER_COVERAGE_FILE")
COVERED_CONDITIONS=$(jq -r '.conditionCounter.covered' "$SWAGGER_COVERAGE_FILE")

TOTAL_ENDPOINTS=$(jq -r '.coverageOperationMap.counter.all' "$SWAGGER_COVERAGE_FILE")
FULL_ENDPOINTS=$(jq -r '.coverageOperationMap.counter.full' "$SWAGGER_COVERAGE_FILE")
PARTIAL_ENDPOINTS=$(jq -r '.coverageOperationMap.counter.party' "$SWAGGER_COVERAGE_FILE")
EMPTY_ENDPOINTS=$(jq -r '.coverageOperationMap.counter.empty' "$SWAGGER_COVERAGE_FILE")

# Check if extraction was successful
if [[ "$TOTAL_CONDITIONS" == "null" || "$COVERED_CONDITIONS" == "null" ]]; then
    echo -e "${RED}❌ Error: Failed to extract coverage data from $SWAGGER_COVERAGE_FILE${NC}"
    exit 1
fi

# Calculate coverage percentage
if [[ "$TOTAL_CONDITIONS" -eq 0 ]]; then
    COVERAGE_PERCENTAGE=0
else
    COVERAGE_PERCENTAGE=$(echo "scale=1; $COVERED_CONDITIONS * 100 / $TOTAL_CONDITIONS" | bc)
fi

ENDPOINT_COVERAGE_PERCENTAGE=$(echo "scale=1; ($FULL_ENDPOINTS + $PARTIAL_ENDPOINTS) * 100 / $TOTAL_ENDPOINTS" | bc)
FULL_COVERAGE_PERCENTAGE=$(echo "scale=1; $FULL_ENDPOINTS * 100 / $TOTAL_ENDPOINTS" | bc)

# Display current coverage metrics
echo ""
echo -e "${BLUE}📈 Current API Coverage Metrics:${NC}"
echo "=================================="
echo -e "🎯 ${YELLOW}Conditions Coverage:${NC} $COVERED_CONDITIONS/$TOTAL_CONDITIONS (${COVERAGE_PERCENTAGE}%)"
echo -e "🏆 ${YELLOW}Full Coverage Endpoints:${NC} $FULL_ENDPOINTS/$TOTAL_ENDPOINTS (${FULL_COVERAGE_PERCENTAGE}%)"
echo -e "⚠️  ${YELLOW}Partial Coverage Endpoints:${NC} $PARTIAL_ENDPOINTS/$TOTAL_ENDPOINTS"
echo -e "❌ ${YELLOW}Empty Coverage Endpoints:${NC} $EMPTY_ENDPOINTS/$TOTAL_ENDPOINTS"
echo -e "📊 ${YELLOW}Overall Endpoint Coverage:${NC} $(($FULL_ENDPOINTS + $PARTIAL_ENDPOINTS))/$TOTAL_ENDPOINTS (${ENDPOINT_COVERAGE_PERCENTAGE}%)"

# Quality Gate Decision
echo ""
echo -e "${BLUE}🚪 Quality Gate Check:${NC}"
echo "======================"
echo -e "📏 ${YELLOW}Minimum Required Coverage:${NC} ${MIN_COVERAGE_THRESHOLD}%"
echo -e "📊 ${YELLOW}Current Conditions Coverage:${NC} ${COVERAGE_PERCENTAGE}%"

# Use integer comparison for bash
COVERAGE_INT=$(echo "$COVERAGE_PERCENTAGE" | cut -d. -f1)

if [[ "$COVERAGE_INT" -ge "$MIN_COVERAGE_THRESHOLD" ]]; then
    echo ""
    echo -e "${GREEN}✅ QUALITY GATE PASSED!${NC}"
    echo -e "${GREEN}🎉 API Coverage ($COVERAGE_PERCENTAGE%) meets minimum threshold ($MIN_COVERAGE_THRESHOLD%)${NC}"
    
    # Optional: Update environment file if it exists  
    if [[ -f "$ALLURE_ENV_FILE" ]]; then
        echo "quality.gate.status=PASSED" >> "$ALLURE_ENV_FILE"
        echo "quality.gate.coverage=$COVERAGE_PERCENTAGE%" >> "$ALLURE_ENV_FILE"
        echo "quality.gate.threshold=$MIN_COVERAGE_THRESHOLD%" >> "$ALLURE_ENV_FILE"
    fi
    
    echo ""
    echo -e "${BLUE}📋 Summary:${NC}"
    echo "- ✅ Coverage meets quality requirements"
    echo "- ✅ Build can proceed"
    echo "- 🎯 Current: ${COVERAGE_PERCENTAGE}% | Required: ${MIN_COVERAGE_THRESHOLD}%"
    
    exit 0
else
    echo ""
    echo -e "${RED}❌ QUALITY GATE FAILED!${NC}"
    echo -e "${RED}🚫 API Coverage ($COVERAGE_PERCENTAGE%) is below minimum threshold ($MIN_COVERAGE_THRESHOLD%)${NC}"
    
    # Optional: Update environment file if it exists
    if [[ -f "$ALLURE_ENV_FILE" ]]; then
        echo "quality.gate.status=FAILED" >> "$ALLURE_ENV_FILE"
        echo "quality.gate.coverage=$COVERAGE_PERCENTAGE%" >> "$ALLURE_ENV_FILE"
        echo "quality.gate.threshold=$MIN_COVERAGE_THRESHOLD%" >> "$ALLURE_ENV_FILE"
    fi
    
    echo ""
    echo -e "${YELLOW}💡 Recommendations to improve coverage:${NC}"
    
    if [[ "$EMPTY_ENDPOINTS" -gt 0 ]]; then
        echo "- 🎯 Focus on endpoints with EMPTY coverage (0% tested)"
        jq -r '.coverageOperationMap.empty[] | "  - \(.httpMethod) \(.path)"' "$SWAGGER_COVERAGE_FILE"
    fi
    
    if [[ "$PARTIAL_ENDPOINTS" -gt 0 ]]; then
        echo "- ⚠️  Improve PARTIAL coverage endpoints (missing status codes, params, etc.)"
        jq -r '.coverageOperationMap.party[] | "  - \(.httpMethod) \(.path)"' "$SWAGGER_COVERAGE_FILE" | head -3
    fi
    
    echo ""
    echo -e "${RED}📋 Summary:${NC}"
    echo "- ❌ Coverage below quality requirements"
    echo "- 🚫 Build should fail"
    echo "- 📉 Gap: $(echo "$MIN_COVERAGE_THRESHOLD - $COVERAGE_PERCENTAGE" | bc)% points needed"
    echo "- 🎯 Current: ${COVERAGE_PERCENTAGE}% | Required: ${MIN_COVERAGE_THRESHOLD}%"
    
    exit 1
fi