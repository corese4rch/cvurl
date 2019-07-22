package coresearch.cvurl.io.request;

import java.net.http.HttpResponse;
import java.util.function.BiFunction;
import java.util.function.Function;

public class StringMappingBodyHandler<T> implements HttpResponse.BodyHandler<T> {

    private Function<String, T> mappingFunction;

    public StringMappingBodyHandler(Function<String, T> mappingFunction) {
        this.mappingFunction = mappingFunction;
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<String> stringBodySubscriber = HttpResponse.BodyHandlers.ofString().apply(responseInfo);
        ;
        return HttpResponse.BodySubscribers.mapping(stringBodySubscriber,
                (str -> mappingFunction.apply(str)));
    }
}
