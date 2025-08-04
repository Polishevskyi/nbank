#!/bin/bash

# Integrate API Coverage with Allure Report
echo "🔄 Integrating API Coverage with Allure..."

# Run swagger coverage
echo "📊 Generating Swagger Coverage..."
.swagger-coverage-commandline/bin/swagger-coverage-commandline \
  -s http://localhost:4111/v3/api-docs \
  -i target/swagger-coverage-output

# Extract metrics from swagger-coverage-results.json
if [ -f "swagger-coverage-results.json" ]; then
    echo "✅ Swagger coverage results found"
    
    # Parse JSON and extract metrics
    TOTAL_CONDITIONS=$(jq '.conditionCounter.all' swagger-coverage-results.json)
    COVERED_CONDITIONS=$(jq '.conditionCounter.covered' swagger-coverage-results.json)
    TOTAL_ENDPOINTS=$(jq '.coverageOperationMap.counter.all' swagger-coverage-results.json)
    FULL_ENDPOINTS=$(jq '.coverageOperationMap.counter.full' swagger-coverage-results.json)
    PARTIAL_ENDPOINTS=$(jq '.coverageOperationMap.counter.party' swagger-coverage-results.json)
    EMPTY_ENDPOINTS=$(jq '.coverageOperationMap.counter.empty' swagger-coverage-results.json)
    
    # Calculate percentages
    CONDITIONS_PERCENTAGE=$(echo "scale=1; $COVERED_CONDITIONS * 100 / $TOTAL_CONDITIONS" | bc)
    FULL_PERCENTAGE=$(echo "scale=1; $FULL_ENDPOINTS * 100 / $TOTAL_ENDPOINTS" | bc)
    PARTIAL_PERCENTAGE=$(echo "scale=1; $PARTIAL_ENDPOINTS * 100 / $TOTAL_ENDPOINTS" | bc)
    EMPTY_PERCENTAGE=$(echo "scale=1; $EMPTY_ENDPOINTS * 100 / $TOTAL_ENDPOINTS" | bc)
    
    echo "📈 API Coverage Summary:"
    echo "   Total Conditions: $COVERED_CONDITIONS/$TOTAL_CONDITIONS ($CONDITIONS_PERCENTAGE%)"
    echo "   Full Coverage: $FULL_ENDPOINTS/$TOTAL_ENDPOINTS endpoints"
    echo "   Partial Coverage: $PARTIAL_ENDPOINTS/$TOTAL_ENDPOINTS endpoints"
    echo "   Empty Coverage: $EMPTY_ENDPOINTS/$TOTAL_ENDPOINTS endpoints"
    
    # Create environment.properties for Allure
    cat > target/allure-results/environment.properties << EOF
# API Coverage Information - Generated $(date)
swagger.coverage.total.conditions=$TOTAL_CONDITIONS
swagger.coverage.covered.conditions=$COVERED_CONDITIONS
swagger.coverage.conditions.percentage=$CONDITIONS_PERCENTAGE%
swagger.coverage.total.endpoints=$TOTAL_ENDPOINTS
swagger.coverage.full.endpoints=$FULL_ENDPOINTS
swagger.coverage.partial.endpoints=$PARTIAL_ENDPOINTS
swagger.coverage.empty.endpoints=$EMPTY_ENDPOINTS
swagger.coverage.full.percentage=$FULL_PERCENTAGE%
swagger.coverage.partial.percentage=$PARTIAL_PERCENTAGE%
swagger.coverage.empty.percentage=$EMPTY_PERCENTAGE%

# Environment Info
test.environment=local
api.base.url=http://localhost:4111
swagger.spec.url=http://localhost:4111/v3/api-docs
report.generated=$(date)
EOF
    
    # Copy HTML report to allure results
    cp swagger-coverage-report.html target/allure-results/ 2>/dev/null || true
    cp swagger-coverage-results.json target/allure-results/ 2>/dev/null || true
    
    # Create synthetic test result for API Coverage Dashboard
    UUID=$(uuidgen 2>/dev/null || echo "api-coverage-$(date +%s)")
    cat > "target/allure-results/${UUID}-result.json" << EOF
{
  "uuid": "$UUID",
  "historyId": "api-coverage-dashboard",
  "name": "API Coverage Dashboard",
  "fullName": "API.Coverage.Dashboard",
  "labels": [
    {"name": "suite", "value": "API Coverage"},
    {"name": "feature", "value": "Swagger Coverage Analysis"},
    {"name": "story", "value": "Coverage Report"}
  ],
  "status": "passed",
  "statusDetails": {
    "message": "API Coverage: $CONDITIONS_PERCENTAGE% ($COVERED_CONDITIONS/$TOTAL_CONDITIONS conditions)",
    "trace": "Full Coverage: $FULL_ENDPOINTS/$TOTAL_ENDPOINTS endpoints\\nPartial Coverage: $PARTIAL_ENDPOINTS/$TOTAL_ENDPOINTS endpoints\\nEmpty Coverage: $EMPTY_ENDPOINTS/$TOTAL_ENDPOINTS endpoints"
  },
  "stage": "finished",
  "start": $(date +%s)000,
  "stop": $(date +%s)000,
  "steps": [
    {
      "name": "📊 Overall API Coverage",
      "status": "passed",
      "start": $(date +%s)000,
      "stop": $(date +%s)000,
      "statusDetails": {
        "message": "Conditions Coverage: $CONDITIONS_PERCENTAGE% ($COVERED_CONDITIONS/$TOTAL_CONDITIONS)"
      }
    },
    {
      "name": "🎯 Endpoint Coverage Breakdown", 
      "status": "passed",
      "start": $(date +%s)000,
      "stop": $(date +%s)000,
      "statusDetails": {
        "message": "Full: $FULL_ENDPOINTS ($FULL_PERCENTAGE%)\\nPartial: $PARTIAL_ENDPOINTS ($PARTIAL_PERCENTAGE%)\\nEmpty: $EMPTY_ENDPOINTS ($EMPTY_PERCENTAGE%)"
      }
    }
  ],
  "attachments": [
    {
      "name": "Swagger Coverage Report",
      "source": "swagger-coverage-report.html",
      "type": "text/html"
    },
    {
      "name": "Coverage Data (JSON)",
      "source": "swagger-coverage-results.json", 
      "type": "application/json"
    }
  ]
}
EOF
    
    echo "✅ API Coverage data integrated with Allure"
    echo "📋 Environment properties updated"
    echo "📊 HTML report copied to Allure results"
    echo "🗂️ API Coverage Dashboard test created"
    
else
    echo "❌ Swagger coverage results not found!"
    exit 1
fi

echo "🎉 API Coverage integration completed successfully!"