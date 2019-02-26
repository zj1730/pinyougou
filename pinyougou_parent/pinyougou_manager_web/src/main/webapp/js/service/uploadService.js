app.service('uploadService',function ($http) {

    this.uploadFile=function () {
        var formDate=new FormData();
        formDate.append("file",file.files[0]);
        return $http({
            method:'POST',
            url:'../upload.do',
            data:formDate,
            headers:{'Content-Type':undefined},
            transformRequest:angular.identity
        });
    };

    /*this.uploadFile=function(){
        var formData=new FormData();
        formData.append("file",file.files[0]);
        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });
    }*/

});