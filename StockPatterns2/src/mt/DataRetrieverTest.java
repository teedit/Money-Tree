package mt;

import junit.framework.TestCase;

public class DataRetrieverTest extends TestCase {
	public void testGetYahooUrl(){
		DataRetriever dr = new DataRetriever();
		
		assertEquals("http://ichart.finance.yahoo.com/table.csv?s=GSPC&d=4&e=27&f=2008&g=d&a=4&b=27&c=2006&ignore=.csv",DataRetriever.getYahooURL("GSPC",800));
	}
}
