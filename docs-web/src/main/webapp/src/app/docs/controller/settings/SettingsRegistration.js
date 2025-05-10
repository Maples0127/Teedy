'use strict';

/**
 * Settings registrations controller.
 */
angular.module('docs').controller('SettingsRegistration', function ($scope, Restangular, $translate, $dialog, $state, $stateParams) {

    /**
     * 提交注册请求（访客用）
     */
    $scope.submitRegistration = function () {
        var registration = angular.copy($scope.registration);
        var promise = Restangular.one('registration').put(registration)

        promise.then(function () {
            $scope.registration = {};
        }, function (e) {
            if (e.data.type === 'AlreadyExistingUsername'){
                var title = $translate.instant('settings.registration.registration_failed_title');
                var msg = $translate.instant('settings.registration.registration_failed_message');
                var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
                $dialog.messageBox(title, msg, btns);
            }
        })
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
});