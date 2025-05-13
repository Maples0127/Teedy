'use strict';

/**
 * Chat controller.
 */
angular.module('docs').controller('UserChat', function($stateParams, Restangular, $scope, $interval) {
    // 初始化变量
    // $scope.targetUser = $stateParams.targetUser; // 从路由参数获取目标用户
    $scope.messages = []; // 使用数组存储消息列表
    $scope.newMessage = '';

    /**
     * 加载历史消息
     */
    $scope.loadMessages = function() {
        Restangular.one('user', $stateParams.username).one('chat').get().then(function(response) {
            $scope.messages = response.userHistory || []; // 确保始终为数组
        });
    };

    /**
     * 发送消息
     */
    $scope.sendMessage = function() {
        if (!$scope.newMessage.trim()) return;

        Restangular.one('user', $stateParams.username).one('chat').put({
            message: $scope.newMessage
        }).then(function() {
            $scope.newMessage = '';
            $scope.loadMessages(); // 正确调用作用域上的方法
        });
    };

    // 初始化加载
    $scope.loadMessages();

    // 使用Angular的$interval服务代替原生setInterval
    var intervalPromise = $interval($scope.loadMessages, 1000);

    // 清除定时器（防止内存泄漏）
    $scope.$on('$destroy', function() {
        if (intervalPromise) $interval.cancel(intervalPromise);
    });
});