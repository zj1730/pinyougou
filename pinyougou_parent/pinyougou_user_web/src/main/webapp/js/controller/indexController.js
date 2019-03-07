app.controller('indexController',function ($scope,loginService) {
    $scope.getName=function () {
        loginService.getName().success(function (response) {
            $scope.username = response.username;
        });
    }
});