package dev.araucaris.hermes.message.codec;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.araucaris.hermes.message.Message;
import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class JacksonMessage extends Message implements Serializable {}
