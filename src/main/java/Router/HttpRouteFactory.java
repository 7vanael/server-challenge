package Router;

import Main.RouteFactory;

import java.nio.file.Path;

public class HttpRouteFactory extends RouteFactory {

    public HttpRouteFactory(Path rootPath, String name) {
        super(rootPath, name);
    }

    @Override
    public void registerRoutes(Router router) {
        router.addRoute("GET", "/", new HomeHandler(rootPath, name));
        router.addRoute("GET", "index.html", new HomeHandler(rootPath, name));
        router.addRoute("GET", "/index.html", new HomeHandler(rootPath, name));
        router.addRoute("GET", "/hello", new HelloHandler(rootPath, name));
        router.addRoute("GET", "/listing", new DirectoryHandler(rootPath, name));
        router.addRoute("GET", "/listing/*", new DirectoryHandler(rootPath, name));
        router.addRoute("GET", "/form", new FormHandler(rootPath, name));
        router.addRoute("POST", "/form", new FormHandler(rootPath, name));
        router.addRoute("GET", "/ping", new PingHandler(rootPath, name));
        router.addRoute("GET", "/guess", new GuessHandler(rootPath, name));
        router.addRoute("POST", "/guess", new GuessHandler(rootPath, name));
        router.addRoute("GET", "/*", new FileHandler(rootPath, name));
    }
}
