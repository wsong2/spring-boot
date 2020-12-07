package swx.springboot.model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MiSerializer extends StdSerializer<MiscItem>
{
	public MiSerializer() {
	    this(null);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6488808530545954068L;

	protected MiSerializer(Class<MiscItem> t)
	{
		super(t);
	}

	@Override
	public void serialize(MiscItem mi, JsonGenerator jgen, SerializerProvider spv) throws IOException
	{
		jgen.writeStartObject();
        jgen.writeNumberField("itemId", mi.getItemId());
        jgen.writeStringField("itemName", mi.getItemName());
        LocalDate itemDate = mi.getItemDate();     
        jgen.writeStringField("itemDate", (itemDate == null) ? "" : itemDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        jgen.writeStringField("descr", mi.getDescr());
        jgen.writeNumberField("value1", mi.getValue1());
        jgen.writeNumberField("value2", mi.getValue2());
        jgen.writeStringField("more", mi.getMore());
        LocalDateTime dttm = mi.getDttm();
        jgen.writeStringField("dttm", (dttm == null) ? "" : dttm.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        jgen.writeEndObject();		
	}
}
