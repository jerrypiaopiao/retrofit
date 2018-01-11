/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2.converter.jaxb;

import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Converter;

import static retrofit2.converter.jaxb.JaxbConverterFactory.IGNORE_UNRECOGNIZED_FIELDS;

final class JaxbRequestConverter<T> implements Converter<T, RequestBody> {
  final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
  final JAXBContext context;
  final Class<T> type;

  JaxbRequestConverter(JAXBContext context, Class<T> type) {
    this.context = context;
    this.type = type;
  }

  @Override public RequestBody convert(final T value) throws IOException {
    return new RequestBody() {
      @Override public MediaType contentType() {
        return JaxbConverterFactory.XML;
      }

      @Override public void writeTo(BufferedSink sink) throws IOException {
        try {
          Marshaller marshaller = context.createMarshaller();
          marshaller.setEventHandler(IGNORE_UNRECOGNIZED_FIELDS);

          XMLStreamWriter xmlWriter = xmlOutputFactory.createXMLStreamWriter(
              sink.outputStream(), JaxbConverterFactory.XML.charset().name());
          marshaller.marshal(value, xmlWriter);
        } catch (JAXBException | XMLStreamException e) {
          throw new XmlDataException(e);
        }
      }
    };
  }
}
