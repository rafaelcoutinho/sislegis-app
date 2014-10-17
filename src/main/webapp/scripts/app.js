'use strict';

angular.module('sislegisapp',['ngRoute','ngResource'])
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/',{templateUrl:'views/landing.html',controller:'LandingPageController'})
      .when('/Comissaos',{templateUrl:'views/Comissao/search.html',controller:'SearchComissaoController'})
      .when('/Comissaos/new',{templateUrl:'views/Comissao/detail.html',controller:'NewComissaoController'})
      .when('/Comissaos/edit/:ComissaoId',{templateUrl:'views/Comissao/detail.html',controller:'EditComissaoController'})
      .when('/Proposicaos',{templateUrl:'views/Proposicao/search.html',controller:'SearchProposicaoController'})
      .when('/Proposicaos/new',{templateUrl:'views/Proposicao/detail.html',controller:'NewProposicaoController'})
      .when('/Proposicaos/edit/:ProposicaoId',{templateUrl:'views/Proposicao/detail.html',controller:'EditProposicaoController'})
      .when('/Reuniaos',{templateUrl:'views/Reuniao/search.html',controller:'SearchReuniaoController'})
      .when('/Reuniaos/new',{templateUrl:'views/Reuniao/detail.html',controller:'NewReuniaoController'})
      .when('/Reuniaos/edit/:ReuniaoId',{templateUrl:'views/Reuniao/detail.html',controller:'EditReuniaoController'})
      .otherwise({
        redirectTo: '/'
      });
  }])
  .controller('LandingPageController', function LandingPageController() {
  })
  .controller('NavController', function NavController($scope, $location) {
    $scope.matchesRoute = function(route) {
        var path = $location.path();
        return (path === ("/" + route) || path.indexOf("/" + route + "/") == 0);
    };
  });
