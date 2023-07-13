# Local Maven Repository 경로
local_occidere_maven_repo='../kailoslab_m2'
mvn clean deploy -DskipTests=true -DaltDeploymentRepository=releases-repo::default::file:${local_occidere_maven_repo}/releases
# Local Maven Repository 로 이동
# shellcheck disable=SC2164
cd ${local_occidere_maven_repo}
echo
echo echo "$PWD"
# git add & commit & push 진행
# deploy key 를 등록했기 때문에 id, pw 입력 없이 진행 가능
git status
git add .
git status
git commit -m "release new version of ai4x"
git push origin main

