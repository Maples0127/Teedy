'use strict';

/**
 * Registrations controller.
 */
angular.module('docs').controller('Registration', function ($scope, Restangular, $translate, $dialog, $state, $stateParams) {
    // 初始化 registration 对象
    $scope.registration = {};  // 新增此行

    /**
     * 加载注册请求（管理员用）—— 先定义函数
     */
    $scope.loadRegistrations = function() {
        Restangular.one('registration').get()
            .then(data => {
                // 注意后端返回的字段名是否为 'registrations' 或 'userRequests'
                $scope.registrations = data.registrations;
            });
    };


    // 只有管理员加载注册请求列表
    if ($scope.userInfo && $scope.userInfo.username === 'admin') {
        $scope.loadRegistrations();
    }

    /**
     * 提交注册请求（访客用）
     */
    $scope.submitRegistration = function () {
        if ($scope.userInfo.username !== 'guest') return;

        const registration = {
            username: $scope.registration.username,
            email: $scope.registration.email
        };

        // 使用表单编码格式发送数据
        Restangular.one('registration')
            .withHttpConfig({ transformRequest: angular.identity })  // 禁用默认JSON转换
            .customPUT($.param(registration), undefined, {}, { 'Content-Type': 'application/x-www-form-urlencoded' })
            .then(() => $scope.registration = {})
            .catch(e => {
                if (e.data.type === 'AlreadyExistingUsername') {
                    $dialog.messageBox(/* ... */);
                }
            });
    };

    /**
     * 处理注册请求（管理员用）
     */
    $scope.processRequest = function (registration, approve) {
        // 确保只有管理员可以处理
        if ($scope.userInfo.username !== 'admin') return;

        Restangular.one('registration/approval', registration.username)
            .post('', { approve: approve })
            .then(() => $scope.loadRegistrations());
    };
});