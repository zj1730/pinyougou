/*不进行分页*/
var app=angular.module('pinyougou',[]);

/*过滤器*/
app.filter('trusthtml',['$sce',function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);