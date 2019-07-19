package coresearch.cvurl.io.multipart;

import coresearch.cvurl.io.constant.HttpHeader;
import coresearch.cvurl.io.constant.MIMEType;
import coresearch.cvurl.io.constant.MultipartType;
import org.junit.jupiter.api.Test;

import java.util.List;

//TODO: add tests
class MultipartBodyTest {

    @Test
    void simpleMultipartBodyTest() {
        //given
        String boundary = "BOUNDARY";

        //when
        List<byte[]> content = MultipartBody.create()
                .multipartType(MultipartType.FORM)
                .formPart("name", Part.of("content"))
                .formPart("name2", Part.of("content2")
                        .header(HttpHeader.CONTENT_TYPE, MIMEType.TEXT_PLAIN))
                .asByteArrays();
    }
}