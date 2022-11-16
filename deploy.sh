# Local Maven Repository 경로
local_occidere_maven_repo='../kailoslab_m2'
mvn deploy -DskipTests=true -DaltDeploymentRepository=snapshot-repo::default::file:${local_occidere_maven_repo}/snapshots
# Local Maven Repository로 이동
# shellcheck disable=SC2164
cd ${local_occidere_maven_repo}
echo
echo echo "$PWD"
# git add & commit & push 진행
# deploy key를 등록했기 때문에 id, pw 입력 없이 진행 가능
git status
git add .
git status
git commit -m "release new version of ai4x"
git push origin main

