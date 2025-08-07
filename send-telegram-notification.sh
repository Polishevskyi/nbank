#!/bin/bash

# Get test results from Allure
if [ -d "target/allure-results" ]; then
    # Count only actual test result files
    TOTAL_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -v "^$" | wc -l)
    PASSED_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "passed" || echo "0")
    FAILED_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "failed" || echo "0")
    BROKEN_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "broken" || echo "0")
    SKIPPED_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "skipped" || echo "0")
    
    # Ensure we don't count empty or invalid results
    if [ "$TOTAL_TESTS" -eq 0 ]; then
        TOTAL_TESTS=$(find target/allure-results -name "*.json" | wc -l)
    fi
else
    TOTAL_TESTS=0
    PASSED_TESTS=0
    FAILED_TESTS=0
    BROKEN_TESTS=0
    SKIPPED_TESTS=0
fi

# Get API coverage
if [ -f "swagger-coverage-results.json" ]; then
    API_COVERAGE=$(jq -r '.conditionCounter.covered // 0' swagger-coverage-results.json)
    API_TOTAL=$(jq -r '.conditionCounter.all // 0' swagger-coverage-results.json)
    if [ "$API_TOTAL" -gt 0 ]; then
        API_PERCENT=$((API_COVERAGE * 100 / API_TOTAL))
    else
        API_PERCENT=0
    fi
else
    API_PERCENT=0
fi

# Calculate success rate and validate statistics
if [ "$TOTAL_TESTS" -gt 0 ]; then
    SUCCESS_RATE=$((PASSED_TESTS * 100 / TOTAL_TESTS))
else
    SUCCESS_RATE=0
fi

# Validate and fix statistics if needed
if [ "$TOTAL_TESTS" -eq 0 ] && [ "$PASSED_TESTS" -gt 0 ]; then
    TOTAL_TESTS=$((PASSED_TESTS + FAILED_TESTS + BROKEN_TESTS + SKIPPED_TESTS))
fi

# Ensure we have at least some tests
if [ "$TOTAL_TESTS" -eq 0 ]; then
    TOTAL_TESTS=1
    SUCCESS_RATE=0
fi

# Determine status emoji and color
if [ "$JOB_STATUS" == "success" ]; then
    STATUS_EMOJI="✅"
    STATUS_TEXT="SUCCESS"
    STATUS_COLOR="🟢"
else
    STATUS_EMOJI="❌"
    STATUS_TEXT="FAILED"
    STATUS_COLOR="🔴"
fi

# Build message with proper escaping
MESSAGE="🚀 <b>CI/CD Pipeline Completed!</b>

📊 <b>Test Statistics:</b>
• Total tests: $TOTAL_TESTS
• Passed: $PASSED_TESTS ✅
• Failed: $FAILED_TESTS ❌
• Success rate: ${SUCCESS_RATE}%
• API coverage: ${API_PERCENT}%

🔗 <b>Links:</b>
• Repository: https://github.com/$GITHUB_REPOSITORY
• Commit: https://github.com/$GITHUB_REPOSITORY/commit/$GITHUB_SHA
• Report: https://$GITHUB_REPOSITORY_OWNER.github.io/$GITHUB_EVENT_REPOSITORY_NAME/

📦 <b>Docker Hub:</b>
• https://hub.docker.com/r/$DOCKER_USERNAME/nbank
• Tag: $GITHUB_SHA

$STATUS_COLOR <b>Status:</b> $STATUS_TEXT"

# Send to Telegram
curl -s -X POST "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/sendMessage" \
  -H "Content-Type: application/json" \
  -d "{
    \"chat_id\": \"$TELEGRAM_CHAT_ID\",
    \"text\": \"$MESSAGE\",
    \"parse_mode\": \"\"
  }"
 