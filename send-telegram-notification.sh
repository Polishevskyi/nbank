#!/bin/bash

# Get test results from Allure
if [ -d "target/allure-results" ]; then
    TOTAL_TESTS=$(find target/allure-results -name "*.json" | wc -l)
    PASSED_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "passed" || echo "0")
    FAILED_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "failed" || echo "0")
    BROKEN_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "broken" || echo "0")
    SKIPPED_TESTS=$(find target/allure-results -name "*.json" -exec jq -r '.status // empty' {} \; | grep -c "skipped" || echo "0")
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

# Calculate success rate
if [ "$TOTAL_TESTS" -gt 0 ]; then
    SUCCESS_RATE=$((PASSED_TESTS * 100 / TOTAL_TESTS))
else
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
MESSAGE="🚀 *CI/CD Pipeline Completed!*

📊 *Test Statistics:*
• Total tests: $TOTAL_TESTS
• Passed: $PASSED_TESTS ✅
• Failed: $FAILED_TESTS ❌
• Broken: $BROKEN_TESTS ⚠️
• Skipped: $SKIPPED_TESTS ⏭️
• Success rate: ${SUCCESS_RATE}%
• API coverage: ${API_PERCENT}%

🔗 *Links:*
• Repository: https://github.com/$GITHUB_REPOSITORY
• Commit: https://github.com/$GITHUB_REPOSITORY/commit/$GITHUB_SHA
• Report: https://$GITHUB_REPOSITORY_OWNER.github.io/$GITHUB_EVENT_REPOSITORY_NAME/

📦 *Docker Hub:*
• docker.io/$DOCKER_USERNAME/nbank:$GITHUB_SHA
• docker.io/$DOCKER_USERNAME/nbank:latest

$STATUS_COLOR *Status:* $STATUS_TEXT"

# Send to Telegram
curl -s -X POST "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/sendMessage" \
  -H "Content-Type: application/json" \
  -d "{
    \"chat_id\": \"$TELEGRAM_CHAT_ID\",
    \"text\": \"$MESSAGE\",
    \"parse_mode\": \"Markdown\"
  }"
