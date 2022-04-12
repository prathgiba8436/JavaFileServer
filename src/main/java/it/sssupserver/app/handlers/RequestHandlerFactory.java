package it.sssupserver.app.handlers;

import it.sssupserver.app.executors.Executor;

/**
 * This class will be used to supply request
 * hadlers based on the supplied parameters
 */
public class RequestHandlerFactory {
    public static RequestHandler getRequestHandler(Executor executor) throws Exception
    {
        return new SimpleBinaryHandler(executor);
    }
}
