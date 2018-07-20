<?php
namespace App\Controller;

use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\ResponseInterface as Response;

final class HomeController extends BaseController
{
    public function dispatch(Request $request, Response $response, $args)
    {
        $this->logger->info("Home page action dispatched");

        $this->flash->addMessage('info', 'Sample flash message');

        $this->view->render($response, 'home.twig');
        return $response;
    }

    public function viewPost(Request $request, Response $response, $args)
    {
        $this->logger->info("View post using Doctrine with Slim 3");

        $messages = $this->flash->getMessage('info');

        try {
            $post = $this->em->find('App\Model\Post', intval($args['id']));
        } catch (\Exception $e) {
            echo $e->getMessage();
            die;
        }

        $this->view->render($response, 'post.twig', ['post' => $post, 'flash' => $messages]);
        return $response;
    }

    public function main(Request $request, Response $response, $args)
    {
        $this->view->render($response, 'index.phtml');
        return $response;
    }
    public function login(Request $request, Response $response, $args)
    {
        $this->view->render($response, 'login.phtml');
        return $response;
    }
    public function register(Request $request, Response $response, $args)
    {
        $this->view->render($response, 'register.phtml');
        return $response;
    }
}
