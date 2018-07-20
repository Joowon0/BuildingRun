<?php
// Routes

$app->get('/', 'App\Controller\HomeController:dispatch')
    ->setName('homepage');

$app->get('/post/{id}', 'App\Controller\HomeController:viewPost')
    ->setName('view_post');

$app->get('/main', 'App\Controller\HomeController:main')
    ->setName('main_page');
$app->get('/login', 'App\Controller\HomeController:login')
    ->setName('login');
$app->get('/register', 'App\Controller\HomeController:register')
    ->setName('register');
