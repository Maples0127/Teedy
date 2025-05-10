'use strict';

/**
 * Settings registrations controller.
 */
angular.module('docs').controller('SettingsRegistration', function ($scope, Restangular, $translate, $dialog, $state, $stateParams) {
    // // 注册请求列表
    // $scope.registrations = [];
    //
    // // 提示信息数组
    // $scope.alerts = [];
    //
    // /**
    //  * 关闭提示
    //  */
    // $scope.closeAlert = function(index) {
    //     $scope.alerts.splice(index, 1);
    // };
    //
    // /**
    //  * 加载所有注册请求
    //  */
    // $scope.loadRegistrations = function() {
    //     Restangular.one('registration').get().then(function(data) {
    //         $scope.registrations = data.userRequests;
    //     }, function() {
    //         $scope.alerts.push({ type: 'danger', msg: $translate.instant('registrations.load_error') });
    //     });
    // };
    //
    // // 初始化加载
    // $scope.loadRegistrations();

    // $scope.isEdit = function () {
    //     return $stateParams.username;
    // };

    /**
     * 提交注册请求（访客用）
     */
    $scope.submitRegistration = function () {
        var registration = angular.copy($scope.registration);
        var promise = Restangular.one('registration').put(registration)

        promise.then(function () {
            $scope.registration = null; // 这个要怎么清空？
        }, function (e) {
            if (e.data.type === 'AlreadyExistingUsername'){
                var title = $translate.instant('settings.registration.registration_failed_title');
                var msg = $translate.instant('settings.registration.registration_failed_message');
                var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
                $dialog.messageBox(title, msg, btns);
            }
        })

        // // 发送PUT请求
        // Restangular.one('registration').customPUT({
        //     username: $scope.user.username,
        //     password: $scope.user.password,
        //     email: $scope.user.email,
        //     storage_quota: $scope.user.storage_quota
        // }).then(function () {
        //     $scope.alerts.push({type: 'success', msg: $translate.instant('registrations.submit_success')});
        //     $scope.user = {}; // 清空表单
        // }, function (e) {
        //     if (e.data.type === 'AlreadyExistingUsername') {
        //         $scope.alerts.push({type: 'danger', msg: $translate.instant('registrations.username_exists')});
        //     } else {
        //         $scope.alerts.push({type: 'danger', msg: $translate.instant('registrations.submit_error')});
        //     }
        // });
    };


    /**
     * 加载所有的注册请求。
     */
    $scope.loadRegistrations = function() {
        Restangular.one('registration').get({
            sort_column: 1,
            asc: true
        }).then(function(data) {
            $scope.registrations = data.registrations;
        });
    };

    $scope.loadRegistrations();





    /**
     * 批准或拒绝请求（管理员用）
     * @param {Object} registration 注册请求
     * @param {boolean} approve 是否批准
     */
    $scope.processRequest = function (registration, approve) {
        let title, msg, btns;

        if (approve) {
            // 批准：弹出表单收集密码和配额
            title = $translate.instant('registrations.approve_title');
            msg = $translate.instant('registrations.approve_message');
            btns = [
                {result: 'cancel', label: $translate.instant('cancel')},
                {result: 'ok', label: $translate.instant('confirm'), cssClass: 'btn-primary'}
            ];

            $dialog.dialog({
                title: title,
                message: msg,
                inputs: [ // 表单字段
                    {type: 'password', label: $translate.instant('password'), model: 'password', required: true},
                    {type: 'number', label: $translate.instant('storage_quota'), model: 'storage_quota', required: true}
                ],
                buttons: btns
            }).then(function (result) {
                if (result === 'ok') {
                    Restangular.one('registration/approval', registration.username).post('', {
                        approve: true,
                        password: result.inputs.password,
                        storage_quota: result.inputs.storage_quota
                    }).then(function () {
                        $scope.loadRegistrations(); // 刷新列表
                    });
                }
            });
        } else {
            // 拒绝：直接发送请求
            Restangular.one('registration/approval', registration.username).post('', {approve: false})
                .then(function () {
                    $scope.loadRegistrations();
                });
        }
    };

    // /**
    //  * 删除注册请求（管理员用）
    //  */
    // $scope.deleteRegistration = function (registration) {
    //     var title = $translate.instant('settings.user.registrations.delete_title');
    //     var msg = $translate.instant('settings.user.registrations.delete_message', {username: registration.username});
    //     var btns = [
    //         {result: 'cancel', label: $translate.instant('cancel')},
    //         {result: 'ok', label: $translate.instant('delete'), cssClass: 'btn-danger'}
    //     ];
    //
    //     $dialog.messageBox(title, msg, btns, function (result) {
    //         if (result === 'ok') {
    //             Restangular.one('registration', registration.username).remove().then(function () {
    //                     $scope.loadRegistrations();
    //                     $state.go('settings.registration');
    //                 }, function (e) {
    //                 if (e.data.type === '') {}
    //                 }
    //             )
    //
    //         }
    //
    //     })
    // };
});