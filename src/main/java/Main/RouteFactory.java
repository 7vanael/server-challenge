package Main;

import Router.Router;

import java.nio.file.Path;

public abstract class RouteFactory {
    protected Path rootPath;
    protected String name;

    public RouteFactory(Path rootPath, String name){
        this.rootPath = rootPath;
        this.name = name;
    }
    public abstract void registerRoutes(Router router);

}
