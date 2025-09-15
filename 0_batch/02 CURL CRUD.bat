@echo on
@set SITE=http://localhost:8080
@set DATA_DIR=./data
@set CURL=c:\tools\curl-7.58.0\bin\curl.exe
@set CURL=%CURL% -g -i -H "Accept: application/hal+json" -H "Content-Type: application/hal+json"
@set HR_YELLOW=@powershell -Command Write-Host "----------------------------------------------------------------------" -foreground "Yellow"
@set HR_RED=@powershell    -Command Write-Host "----------------------------------------------------------------------" -foreground "Red"
@set QUEST_ID=1

:delete
%HR_YELLOW%
@powershell -Command Write-Host "DELETE QUESTIONNAIRE by id" -foreground "Green"
%CURL% -X DELETE "%SITE%/questionnaires/%QUEST_ID%"
@echo.
goto :create

:read-deleted
%HR_YELLOW%
@powershell -Command Write-Host "READ DATA AFTER DELETE" -foreground "Green"
%CURL% "%SITE%/questionnaires/"
%CURL% "%SITE%/inboxes/"
%CURL% "%SITE%/respondentReplies/"
@echo.
pause

:create
%HR_YELLOW%
@powershell -Command Write-Host "CREATE QUESTIONNAIRE" -foreground "Green"
for /F %%I in ('%CURL% -d @%DATA_DIR%/questionnaire_from_alice.json -X POST "%SITE%/create_questionnaire"') do @set SIGNATURE=%%I
@echo QUESTIONER SIGNATURE: %SIGNATURE%
@echo.

:read-questionnaire-created
%HR_YELLOW%
@powershell -Command Write-Host "READ CREATED QUESTIONNAIRE" -foreground "Green"
%CURL% "%SITE%/questionnaires/%QUEST_ID%"
@echo.
@powershell -Command Write-Host "READ AS RESPONDENT" -foreground "Green"
%CURL% "%SITE%/read_questionnaire_as_respondent_path?id=%QUEST_ID%"
@echo.&pause

:post-replies
%HR_YELLOW%
@powershell -Command Write-Host "POST REPLIES TO QUESTIONNAIRE" -foreground "Green"
%CURL% -d @%DATA_DIR%/questionnaire_from_bob.json -X POST "%SITE%/post_replies"
@echo.
%CURL% -d @%DATA_DIR%/questionnaire_from_charlie.json -X POST "%SITE%/post_replies"
@echo.
%CURL% -d @%DATA_DIR%/questionnaire_from_anonymous.json -X POST "%SITE%/post_replies"
@echo.

:read-questionnaire-replied
%HR_YELLOW%
@powershell -Command Write-Host "READ QUESTIONNAIRE AS QUESTIONER" -foreground "Green"
%CURL% "%SITE%/read_questionnaire_as_questioner_path?id=%QUEST_ID%&ownerSignature=%SIGNATURE%"
@echo.

:finish
%HR_RED%
@powershell -Command Write-Host "FINISH" -foreground "Red"
pause