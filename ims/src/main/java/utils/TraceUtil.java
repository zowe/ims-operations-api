/**
 *  Copyright IBM Corporation 2018, 2019
 */

package utils;

public class TraceUtil {
	/**
	 * Return a string represetnation of each byte of data in
	 * the buffer represented in HEX.
	 *
	 * <p>The string is formatted with a count of the bytes on
	 * the right column. Each row would display 16 bytes of the
	 * data, each represented in their HEX value.  If a character
	 * is can be identified, it will be displayed in the middle
	 * column (converted with Cp037).
	 *
	 * @param buffer The data buffer to be displayed
	 */
	public static String dumpBytesInHex(byte[] buffer)
	{
		StringBuffer strbuf =
			new StringBuffer(buffer.length * 4 + (buffer.length / 16) * 6);

		strbuf.append("\t[\n\t\t");

		int rowCount = 0;
		int totalRowCount = 0;
		for (int i = 0; i < buffer.length; i++)
		{
			int j = ((buffer[i] & 0xF0) >> 4);
			int k = (buffer[i] & 0x0F);

			strbuf.append(Integer.toString(j, 16));
			strbuf.append(Integer.toString(k, 16));

			rowCount++;
			totalRowCount++;

			if (rowCount % 4 == 0)
				strbuf.append(" ");

			if (rowCount >= 16)
			{
				try
				{
					String str =
						new String(buffer, i - rowCount + 1, rowCount, "cp037");
					strbuf.append("|");
					for (int m = 0; m < rowCount; m++)
					{
						if (Character.isIdentifierIgnorable(str.charAt(m)))
							strbuf.append(".");
						else
							strbuf.append(str.charAt(m));
					}
					strbuf.append("|");
				}
				catch (Exception e)
				{
				}

				strbuf.append(" : ");
				strbuf.append(totalRowCount);
				strbuf.append("\n\t\t");
				rowCount = 0;
			}
		}

		if (rowCount != 0)
		{
			for (int l = rowCount; l < 16; l++)
			{
				strbuf.append("  ");

				if ((l + 1) % 4 == 0)
					strbuf.append(" ");
			}
			try
			{
				String str =
					new String(buffer, buffer.length - rowCount, rowCount, "cp037");
				strbuf.append("|");
				for (int m = 0; m < rowCount; m++)
				{
					if (Character.isIdentifierIgnorable(str.charAt(m)))
						strbuf.append(".");
					else
						strbuf.append(str.charAt(m));
				}
				strbuf.append("|");
			}
			catch (Exception e)
			{
			}
		}

		strbuf.append("\n\t]");

		return strbuf.toString();
	}
}
