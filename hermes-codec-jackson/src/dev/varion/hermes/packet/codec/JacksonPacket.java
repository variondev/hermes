package dev.varion.hermes.packet.codec;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.varion.hermes.packet.Packet;
import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class JacksonPacket extends Packet implements Serializable {}
