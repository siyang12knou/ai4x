/*
   SDNO version 1.0

   Copyright © 2019 kt corp. All rights reserved.

   This is a proprietary software of kt corp, and you may not user this file except in
   compliance with license agreement with kt corp. Any redistribution or use of this
   software, with or without modification shall be strictly prohibited without prior written
   approval of kt corp, and the copyright notice above does not evidence any actual or
   intended publication of such software.
*/

package com.kailoslab.ai4x.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serial;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateSerializer extends StdSerializer<Date> {

    @Serial
    private static final long serialVersionUID = 1L;

    public DateSerializer() {
        this(null);
    }

    public DateSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        LocalDateTime localDateTime = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String formattedDateTime = DateTimeFormatter.ofPattern(Constants.df).format(localDateTime);
        gen.writeString(formattedDateTime);
    }

}
