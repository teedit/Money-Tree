package ibd.threads;

import java.util.Calendar;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;


public class IBDCalendar extends java.util.GregorianCalendar
{
   	// used for the localization convenience methods
	private DateFormatSymbols dfs = new DateFormatSymbols();


	// format used in creation
	public static final int WORKDAYS = 999;

	// format used in creation
	private int format = -1;

	/**
	 * the id for Autoliv's standard Oracle format.
	 */
	public static final int ORACLE = 7;


	/**
	 * the id for Autoliv's standard
	 */
	public static final int DATE_FULL = 46;

	/**
	 * the id for Autoliv's standard
	 */
	public static final int DATE = 128;

	/**
	 * the id for Autoliv's standard
	 */
	public static final int TIME = 146;

	/**
	 * the formats that we will be able to parse.
	 * NOTE: If you add to this, you MUST place the format correctly into the
	 * array.  It MUST BE AFTER more detailed formats AND BEFORE less detailed
	 * ones.
	 */
	public static final String[] formats = new String[] {
			"yyyy-MM-dd hh:mm:ss.SSS a zzz", // 0
			"yyyy-MM-dd HH:mm:ss.SSS zzz",
			"yyyy-MM-dd hh:mm:ss.SSS a",
			"yyyy-MM-dd HH:mm:ss.SSS",
			"yyyy-MM-dd hh:mm:ss a zzz",
			"yyyy-MM-dd HH:mm:ss zzz",
			"yyyy-MM-dd hh:mm:ss a",
			"yyyy-MM-dd HH:mm:ss", // our Oracle date, or should we use the tz?
			"yyyy-MM-dd hh:mm a zzz",
			"yyyy-MM-dd HH:mm zzz",
			"yyyy-MM-dd hh:mm a", // 10
			"yyyy-MM-dd HH:mm",
			"yyyy-MM-dd'T'HH:mm:ss.SSS", // sort of ISO 8601
			"yyyy-MM-dd'T'HH:mm:ss", // ISO 8601
			"yyyy-MM-dd'T'HH:mm", // ISO 8601
			"yyyy/MM/dd hh:mm:ss.SSS a zzz",
			"yyyy/MM/dd HH:mm:ss.SSS zzz",
			"yyyy/MM/dd hh:mm:ss.SSS a",
			"yyyy/MM/dd HH:mm:ss.SSS",
			"yyyy/MM/dd hh:mm:ss a zzz",
			"yyyy/MM/dd HH:mm:ss zzz", // 20
			"yyyy/MM/dd hh:mm:ss a",
			"yyyy/MM/dd HH:mm:ss",
			"yyyy/MM/dd hh:mm a zzz",
			"yyyy/MM/dd HH:mm zzz",
			"yyyy/MM/dd hh:mm a",
			"yyyy/MM/dd HH:mm",
			"dd-MM-yyyy hh:mm:ss.SSS a zzz",
			"dd-MM-yyyy HH:mm:ss.SSS zzz",
			"dd-MM-yyyy hh:mm:ss.SSS a",
			"dd-MM-yyyy HH:mm:ss.SSS", // 30
			"dd-MM-yyyy hh:mm:ss a zzz",
			"dd-MM-yyyy HH:mm:ss zzz",
			"dd-MM-yyyy hh:mm:ss a",
			"dd-MM-yyyy HH:mm:ss",
			"dd-MM-yyyy hh:mm a zzz",
			"dd-MM-yyyy HH:mm zzz",
			"dd-MM-yyyy hh:mm a",
			"dd-MM-yyyy HH:mm",
			"MM/dd/yyyy hh:mm:ss.SSS a zzz",
			"MM/dd/yyyy HH:mm:ss.SSS zzz", // 40
			"MM/dd/yyyy hh:mm:ss.SSS a",
			"MM/dd/yyyy HH:mm:ss.SSS",
			"MM/dd/yyyy hh:mm:ss a zzz",
			"MM/dd/yyyy HH:mm:ss zzz",
			"MM/dd/yyyy hh:mm:ss a",
			"MM/dd/yyyy HH:mm:ss", // DISPLAY_FULL
			"MM/dd/yyyy hh:mm a zzz",
			"MM/dd/yyyy HH:mm zzz",
			"MM/dd/yyyy hh:mm a",
			"MM/dd/yyyy HH:mm", // 50
			"EEE dd MMM yyyy hh:mm:ss.SSS a zzz",
			"EEE dd MMM yyyy HH:mm:ss.SSS zzz",
			"EEE dd MMM yyyy hh:mm:ss.SSS a",
			"EEE dd MMM yyyy HH:mm:ss.SSS",
			"EEE dd MMM yyyy hh:mm:ss a zzz",
			"EEE dd MMM yyyy HH:mm:ss zzz",
			"EEE dd MMM yyyy hh:mm:ss a",
			"EEE dd MMM yyyy HH:mm:ss",
			"EEE dd MMM yyyy hh:mm a zzz",
			"EEE dd MMM yyyy HH:mm zzz", // 60
			"EEE dd MMM yyyy hh:mm a",
			"EEE dd MMM yyyy HH:mm",
			"EEE, MMM dd, yyyy hh:mm:ss.SSS a zzz",
			"EEE, MMM dd, yyyy HH:mm:ss.SSS zzz",
			"EEE, MMM dd, yyyy hh:mm:ss.SSS a",
			"EEE, MMM dd, yyyy HH:mm:ss.SSS",
			"EEE, MMM dd, yyyy hh:mm:ss a zzz",
			"EEE, MMM dd, yyyy HH:mm:ss zzz",
			"EEE, MMM dd, yyyy hh:mm:ss a",
			"EEE, MMM dd, yyyy HH:mm:ss", // 70
			"EEE, MMM dd, yyyy hh:mm a zzz",
			"EEE, MMM dd, yyyy HH:mm zzz",
			"EEE, MMM dd, yyyy hh:mm a",
			"EEE, MMM dd, yyyy HH:mm",
			"MMM dd, yyyy hh:mm:ss.SSS a zzz",
			"MMM dd, yyyy HH:mm:ss.SSS zzz",
			"MMM dd, yyyy hh:mm:ss.SSS a",
			"MMM dd, yyyy HH:mm:ss.SSS",
			"MMM dd, yyyy hh:mm:ss a zzz",
			"MMM dd, yyyy HH:mm:ss zzz", // 80
			"MMM dd, yyyy hh:mm:ss a",
			"MMM dd, yyyy HH:mm:ss",
			"MMM dd, yyyy hh:mm a zzz",
			"MMM dd, yyyy HH:mm zzz",
			"MMM dd, yyyy hh:mm a",
			"MMM dd, yyyy HH:mm",
			"MMM dd yyyy hh:mm:ss.SSS a zzz",
			"MMM dd yyyy HH:mm:ss.SSS zzz",
			"MMM dd yyyy hh:mm:ss.SSS a",
			"MMM dd yyyy HH:mm:ss.SSS", // 90
			"MMM dd yyyy hh:mm:ss a zzz",
			"MMM dd yyyy HH:mm:ss zzz",
			"MMM dd yyyy hh:mm:ss a",
			"MMM dd yyyy HH:mm:ss",
			"MMM dd yyyy hh:mm a zzz",
			"MMM dd yyyy HH:mm zzz",
			"MMM dd yyyy hh:mm a",
			"MMM dd yyyy HH:mm",
			"dd MMM yyyy hh:mm:ss.SSS a zzz",
			"dd MMM yyyy HH:mm:ss.SSS zzz", // 100
			"dd MMM yyyy hh:mm:ss.SSS a",
			"dd MMM yyyy HH:mm:ss.SSS",
			"dd MMM yyyy hh:mm:ss a zzz",
			"dd MMM yyyy HH:mm:ss zzz",
			"dd MMM yyyy hh:mm:ss a",
			"dd MMM yyyy HH:mm:ss",
			"dd MMM yyyy hh:mm a zzz",
			"dd MMM yyyy HH:mm zzz",
			"dd MMM yyyy hh:mm a",
			"dd MMM yyyy HH:mm", // 110
			"dd-MMM-yyyy hh:mm:ss.SSS a zzz",
			"dd-MMM-yyyy HH:mm:ss.SSS zzz",
			"dd-MMM-yyyy hh:mm:ss.SSS a",
			"dd-MMM-yyyy HH:mm:ss.SSS",
			"dd-MMM-yyyy hh:mm:ss a zzz",
			"dd-MMM-yyyy HH:mm:ss zzz",
			"dd-MMM-yyyy hh:mm:ss a",
			"dd-MMM-yyyy HH:mm:ss",
			"dd-MMM-yyyy hh:mm a zzz",
			"dd-MMM-yyyy HH:mm zzz", // 120
			"dd-MMM-yyyy hh:mm a",
			"dd-MMM-yyyy HH:mm",
			"EEE, MMM dd, yyyy",
			"EEE dd MMM yyyy",
			"yyyy/MM/dd",
			"yyyy-MM-dd",
			"dd-MM-yyyy",
			"MM/dd/yyyy", // Date
			"dd-MMM-yyyy",
			"dd MMM yyyy", // 130
			"MMM dd, yyyy",
			"MMM dd yyyy",
			"MMM-yyyy",
			"yyyy-MM", // short date
			"MM-yyyy",
			"yyyy/MM",
			"MM/yyyy",
			"MMM yyyy",
			"hh:mm:ss.SSS a zzz",
			"HH:mm:ss.SSS zzz", // 140
			"hh:mm:ss.SSS a",
			"HH:mm:ss.SSS",
			"hh:mm:ss a zzz",
			"HH:mm:ss zzz",
			"hh:mm:ss a",
			"HH:mm:ss", // TIME
			"hh:mm a zzz",
			"HH:mm zzz",
			"hh:mm a",
			"HH:mm", // 150
			"yyyy", // year only
			"MM/dd/yy", //152
			"dd", //153
			"MM", //154
			"HH",
			"ddMMMyyyy",
         "CCCDDD",  //JDE Date
			"CCCDDD-HHmmss",//JDE Date with time 158
		   "HHmmss"	}; //JDE Time with time 159

	/**
	 * Allocates an <code>IBDCalendar</code> object and initializes it so that
	 * it represents the time at which it was allocated, measured to the
	 * nearest millisecond.
	 *
	 * @see java.util.GregorianCalendar#GregorianCalendar()
	 */
	public IBDCalendar()
	{
		super();
	}

	/**
	 * Allocates an <code>IBDCalendar</code> object and initializes it to
	 * represent the specified number of milliseconds since the
	 * standard base time known as "the epoch", namely January 1,
	 * 1970, 00:00:00 GMT.
	 *
	 * @param date the milliseconds since January 1, 1970, 00:00:00 GMT.
	 * @see  java.util.GregorianCalendar#setTimeInMillis(long)
	 */
	public IBDCalendar(long date)
	{
		super.setTimeInMillis(date);
	}

	/**
	 * Allocates an <code>IBDCalendar</code> object and initializes it to
	 * represent the dateString specified.
	 *
	 * @param dateString the string to parse
	 * @exception IllegalArgumentException if the date is not parseable.
	 */
	public IBDCalendar(String dateString)
	{
		super();
		setValue(dateString);
	}

	/**
	 * Allocates an <code>IBDCalendar</code> object and initializes it to
	 * represent the java.sql.Date specified.
	 *
	 * @param date the java.sql.Date object
	 */
	public IBDCalendar(java.sql.Date date)
	{
		super.setTimeInMillis(date.getTime());
	}

	/**
	 * Allocates an <code>IBDCalendar</code> object and initializes it to
	 * represent the java.sql.Time specified.
	 * @param time the java.sql.Time object
	 */
	public IBDCalendar(java.sql.Time time)
	{
		super.setTimeInMillis(time.getTime());
	}

	/**
	 * Allocates an <code>IBDCalendar</code> object and initializes it to
	 * represent the java.sql.Timestamp specified.
	 *
	 * @param timestamp the java.sql.Timestamp object
	 */
	public IBDCalendar(java.sql.Timestamp timestamp)
	{
		super.setTimeInMillis(timestamp.getTime());
	}

	/**
	 * Allocates an <code>IBDCalendar</code> object and initializes it to
	 * represent the <code>dateString</code> specified, formatted according
	 * to <code>format</code>.
	 *
	 * @param dateString the string to parse
	 * @param format the format of the dateString
	 * @exception IllegalArgumentException if the date is not parseable
	 */
	public IBDCalendar(String dateString, String format)
	{
		super();
		format(dateString,format);
	}

	/**
	 * Allocates an <code>IBDCalendar</code> object and initializes it to
	 * represent the Calendar specified.
	 *
	 * @param calendar Calendar object
	 */
	public IBDCalendar(Calendar calendar)
	{
		super.setTime(calendar.getTime());
	}

	/**
	 * Allocates an <code>IBDCalendar</code> object and initializes it to
	 * represent the java.util.Date specified.
	 *
	 * @param date the java.sql.Date object
	 */
	public IBDCalendar(java.util.Date date)
	{
		super.setTimeInMillis(date.getTime());
	}

	/**
	 * returns a java.sql.Date object of this <code>date</code>.
	 * @see java.sql.Date
	 * @return java.sql.Date of this object
	 */
	public java.sql.Date asDate()
	{
		return new java.sql.Date(this.getTimeInMillis());
	}

	/**
	 * returns a java.sql.Time object of this <code>date</code>.
	 *
	 * @see java.sql.Time
	 * @return java.sql.Time of this object
	 */
	public java.sql.Time asTime()
	{
		return new java.sql.Time(this.getTimeInMillis());
	}

	/**
	 * returns a java.sql.Timestamp object of this <code>date</code>.
	 *
	 * @see java.sql.Timestamp
	 * @return java.sql.Timestamp of this object
	 */
	public java.sql.Timestamp asTimestamp()
	{
		return new java.sql.Timestamp(this.getTimeInMillis());
	}

	/**
	 * sets the date object to <code>date</code>.
	 *
	 * @param date java.sql.Date representation of a date object
	 */
	public void setValue(java.sql.Date date)
	{
		this.setTimeInMillis(date.getTime());
	}

	/**
	 * sets the date object to <code>time</code>.
	 *
	 * @param time java.sql.Time representation of a date object
	 */
	public void setValue(java.sql.Time time)
	{
		this.setTimeInMillis(time.getTime());
	}

	/**
	 * sets the date object to <code>timestamp</code>.
	 *
	 * @param timestamp java.sql.Timestamp representation of a date object
	 */
	public void setValue(java.sql.Timestamp timestamp)
	{
		this.setTimeInMillis(timestamp.getTime());
	}

	/**
	 * sets the date object to <code>dateString</code>.
	 *
	 * @param dateString String representation of a date object
	 * @exception IllegalArgumentException if dateString is not parseable
	 */
	public void setValue(String dateString)
	{
		IllegalArgumentException e = null;
		for (int i = 0; i < formats.length; i++)
		{
			try
			{
				format(dateString,formats[i]);
				format = i;
				return;
			}
			catch (IllegalArgumentException iae)
			{
				e = iae;
			}
			catch (StringIndexOutOfBoundsException sioobe)
			{
				// just skip it! This is necessary for 1.1.8
			}
		}
		throw e;
	}

	public long getTimeInMillis()
	{
		return super.getTimeInMillis();
	}

	public void setTimeInMillis(long time)
	{
		super.setTimeInMillis(time);
	}

	/**
	 * determines the difference between two IBDCalendar objects
	 *
	 * @param field the time field.  These are inherited (ultimately) from
	 * IBDCalendar.  Valid values are:<ul>
	 * <li> YEAR returns the number of years, 1/1/2001 - 12/31/2000 = 1 year.
	 * <li> MONTH returns the number of months, 2/1/2001 - 1/31/2001 = 1 month.
	 * <li> WEEK_OF_YEAR, WEEK_OF_MONTH, DAY_OF_WEEK_IN_MONTH returns the number of
	 *      weeks between dates.
	 * <li> DATE, DAY_OF_YEAR, DAY_OF_WEEK returns the number of days between dates.
	 * <li> HOUR, HOUR_OF_DAY returns the number of hours between dates.
	 * <li> MINUTE returns the number of minutes between dates.
	 * <li> SECOND returns the number of seconds between dates.
	 * <li> MILLISECOND returns the number of milliseconds between dates.
	 * </ol>
	 * @param other the other IBDCalendar
	 * @exception IllegalArgumentException if an unknown field is given.
	 * @return long Time difference between IBDCalendar objedts
	 */

	public long difference(int field, IBDCalendar other)
	{
		if (this == other)
		{
			return 0;
		}

		long difference = getTimeInMillis() - other.getTimeInMillis();
		switch (field)
		{
			case YEAR:
				difference = get(YEAR) - other.get(YEAR);
				break;
			case MONTH:
				difference =
					((get(YEAR) - other.get(YEAR)) * 12) +
					(get(MONTH) - other.get(MONTH) );
				break;
				// notice no use of breaks! this cascades
			case WEEK_OF_YEAR:
			case WEEK_OF_MONTH:
			case DAY_OF_WEEK_IN_MONTH:
				difference /= 7;
			case DATE:
			case WORKDAYS:
			case DAY_OF_YEAR:
			case DAY_OF_WEEK:
				difference /= 24;
			case HOUR:
			case HOUR_OF_DAY:
				difference /= 60;
			case Calendar.MINUTE:
				difference /= 60;
			case Calendar.SECOND:
				difference /= 1000;
			case Calendar.MILLISECOND:
				// do nothing, already in milliseconds
				break;
			default:
				throw new IllegalArgumentException("Illegal Argument: " + field);
		}
		if(field == WORKDAYS)
		{
		  double weeks = ((double)difference)/7;
		  difference = (long)(weeks*5);
		}
		return difference;
	}

	/**
	 * Converts this <code>Date</code> object to a <code>String</code>
	 * of the form <code>formats[ORACLE]</code>.
	 *
	 * @return String representation of this Date.
	 */
	public String toString()
	{
		return toString(IBDCalendar.DATE);
	}

	/**
	 * Converts this <code>Date</code> object to a <code>String</code>
	 * of the form <code>formats[formatType]</code>.
	 *
	 * @param formatType the format for parsing the date.
	 * @return String representation of this Date.
	 * @exception ArrayIndexOutOfBoundsException on formatType error.
	 */
	public String toString(int formatType)
	{
		if (formatType < 0 || formatType > formats.length)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		return toString(formats[formatType]);
	}

	/**
	 * Converts this <code>Date</code> object to a <code>String</code>
	 * of the form <code>format</code>.
	 *
	 * For more information on the format string, see {@link java.text.DateFormat}.
	 * @param format the format for parsing the date.
	 * @return String representation of this Date.
	 * @see java.text.SimpleDateFormat
	 */
	public String toString(String format)
	{
	   if("CCCDDD".equals(format))
		{
		  String yr = toString("yyyy");
		  int intyr = Integer.parseInt(yr);
		  if(intyr < 100)
		  {
			 yr="0"+Integer.toString(intyr-1900);
		  }
		  else
		  {
			 yr=Integer.toString(intyr-1900);
		  }
   	  return  yr + toString("DDD");
		}
		else if("CCCDDDHHmmss".equals(format))
		{
		  String yr = toString("yyyy");
		  int intyr = Integer.parseInt(yr);
		  if(intyr < 100)
		  {
			 yr="0"+Integer.toString(intyr-1900);
		  }
		  else
		  {
			 yr=Integer.toString(intyr-1900);
		  }
		  return yr + toString("DDDHHmmss");

		}
		else
		{
		  SimpleDateFormat sdf = new SimpleDateFormat(format);
		  return sdf.format(getTime());
		}
	}


	/**
	 * returns the id used in parsing the String received.
	 * of the form <code>format</code>.
	 *
	 * @return int of the format id, or -1 if formats weren't used.
	 */
	public int getFormatId()
	{
		return format;
	}

	/**
	 * returns the format String used in parsing the String received.
	 * @param formatId int for the fomat
	 * @return String format, or null if the id is out of range.
	 */
	public String getFormat(int formatId)
	{
		if (formatId > formats.length || formatId < 0)
		{
			return null;
		}
		return formats[formatId];
	}

	/**
	 * returns the Era for this <code>date</code>.
	 * This is a convenience method, and is localized.
	 *
	 * @return String Era
	 */
	public String getEraName()
	{
		String eras[] = dfs.getEras();
		return eras[get(IBDCalendar.ERA)];
	}

	/**
	 * returns the MonthName for this <code>date</code>.
	 * This is a convenience method, and is localized.
	 *
	 * @return String MonthName (long)
	 */
	public String getMonthName()
	{
		String months[] = dfs.getMonths();
		return months[get(IBDCalendar.MONTH)];
	}

	/**
	 * returns the MonthName short (three chars) for this <code>date</code>.
	 * This is a convenience method, and is localized.
	 *
	 * @return String MonthName (short)
	 */
	public String getShortMonthName()
	{
		String shortMonths[] = dfs.getShortMonths();
		return shortMonths[get(IBDCalendar.MONTH)];
	}

	/**
	 * returns the WeekdayName for this <code>date</code>.
	 * This is a convenience method, and is localized.
	 *
	 * @return String WeekdayName (long)
	 */
	public String getWeekdayName()
	{
		String days[] = dfs.getWeekdays();
		return days[get(IBDCalendar.DAY_OF_WEEK)];
	}

	/**
	 * returns the WeekdayName short (three chars) for this <code>date</code>.
	 * This is a convenience method, and is localized.
	 *
	 * @return String WeekdayName (short)
	 */
	public String getShortWeekdayName()
	{
		String shortWeekdays[] = dfs.getShortWeekdays();
		return shortWeekdays[get(IBDCalendar.DAY_OF_WEEK)];
	}

	/**
	 * returns the AM/PM indicator for this <code>date</code>.
	 * This is a convenience method, and is localized.
	 *
	 * @return String AM/PM indicator
	 */
	public String getAmpmName()
	{
		String ampms[] = dfs.getAmPmStrings();
		return ampms[get(IBDCalendar.AM_PM)];
	}

	/**
	 * returns the TimeZone for this <code>date</code>.
	 * This is a convenience method, and is localized.
	 * @todo fix this to work with either 1.1.8 and 1.2
	 * @return String TimeZone (long)
	 */
	public String getLongZoneName()
	{
		TimeZone tz = getTimeZone();
		boolean daylight = tz.inDaylightTime(getTime());
// jdk1.2
//		return tz.getDisplayName(daylight,tz.LONG);

		return tz.getID();
	}

	/**
	 * returns the TimeZone short (three chars) for this <code>date</code>.
	 * This is a convenience method, and is localized.
	 * @todo fix this to work with either 1.1.8 and 1.2
	 * @return String TimeZone (short)
	 */
	public String getShortZoneName()
	{
		TimeZone tz = getTimeZone();
		boolean daylight = tz.inDaylightTime(getTime());
// jdk1.2
//		return tz.getDisplayName(daylight,tz.SHORT);

		return tz.getID();
	}

	/*
	 * our nice little workhorse of the IBDCalendar class
	 */
	private void format(String dateString, String format)
	{
	  if(format.equals("CCCDDD"))
	  {
		int cyear = Integer.parseInt(dateString.substring(0,dateString.length()-3));
		dateString=Integer.toString(cyear + 1900) + dateString.substring(dateString.length()-3);
		format = "yyyyDDD";
	  }
	  else if(format.equals("CCCDDD-HHmmss"))
	  {
		 int indx = dateString.indexOf("-");
		 String yearStr = dateString.substring(0,indx);
		 int cyear = Integer.parseInt(yearStr.substring(0,yearStr.length()-3));
		 String timeStr = dateString.substring(indx+1);
		 if(timeStr.length() == 5)
		 {
			timeStr= "0" + timeStr;
		 }
		 dateString=Integer.toString(cyear + 1900) + yearStr.substring(yearStr.length()-3) + timeStr;
		 format = "yyyyDDDHHmmss";
	  }


	  SimpleDateFormat sdf = new SimpleDateFormat(format);
	  sdf.setLenient(false); // this MUST BE SET!
	  try
	  {
		 long val = sdf.parse(dateString).getTime();
		 setTimeInMillis(val);
	  }
	  catch (java.text.ParseException pe)
	  {
		 throw new IllegalArgumentException(pe.toString());
	  }
	}
			/**
			 @return The Julian day number that begins at noon of
			 this day
			 Positive year signifies A.D., negative year B.C.
			 Remember that the year after 1 B.C. was 1 A.D.

			 A convenient reference point is that May 23, 1968 noon
			 is Julian day 2440000.

			 Julian day 0 is a Monday.

			 This algorithm is from Press et al., Numerical Recipes
			 in C, 2nd ed., Cambridge University Press 1992
		 */

		public int toJulian()
		{
			int jul = 0;
			try{
				int year = get(Calendar.YEAR);
				int month = get(Calendar.MONTH);
				int day = get(Calendar.DAY_OF_MONTH);
				int jy = year;
				if ( year < 0 ) jy++;
				int jm = month;
				if ( month > 2 ) jm++;
				else
				{
						jy--;
						jm += 13;
				}
				jul = (int) (java.lang.Math.floor(365.25 * jy)
												 + java.lang.Math.floor(30.6001*jm) + day + 1720995.0);

				int IGREG = 15 + 31*(10+12*1582);
				// Gregorian Calendar adopted Oct. 15, 1582

				if ( day + 31 * (month + 12 * year) >= IGREG )
				// change over to Gregorian calendar
				{
						int ja = (int)(0.01 * jy);
						jul += 2 - ja + (int)(0.25 * ja);
				}
			}catch(Exception e)
			{
				return 0;
			}
				return jul;
		}

		/**
			 Converts a Julian day to a calendar date

			 This algorithm is from Press et al., Numerical Recipes
			 in C, 2nd ed., Cambridge University Press 1992
			 @param j  the Julian date
			 @return IBDCalendar
		 */

		public static IBDCalendar fromJulian(int j)
		{
				int ja = j;
				int JGREG = 2299161;
				/* the Julian date of the adoption of the Gregorian
					 calendar
				*/

				if ( j >= JGREG )
				/* cross-over to Gregorian Calendar produces this
					 correction
				*/
				{
						int jalpha = (int)(((float)(j - 1867216) - 0.25)
															 / 36524.25);
						ja += 1 + jalpha - (int)(0.25 * jalpha);
				}
				int jb = ja + 1524;
				int jc = (int)(6680.0 + ((float)(jb-2439870) - 122.1)
											 /365.25);
				int jd = (int)(365 * jc + (0.25 * jc));
				int je = (int)((jb - jd)/30.6001);
				int day = jb - jd - (int)(30.6001 * je);
				int month = je - 1;
				if ( month > 12 ) month -= 12;
				int year = jc - 4715;
				if ( month > 2 ) --year;
				if ( year <= 0 ) --year;
				IBDCalendar c = new IBDCalendar();
				c.set(year,month,day);
				return c;
		}


		public static void main(String[] args)
		{
		  IBDCalendar ac = new IBDCalendar("104040", "CCCDDD");
		  System.out.println(ac.toString(IBDCalendar.DATE_FULL));
		  System.out.println(ac.toString(157));
		  IBDCalendar ac1 = new IBDCalendar("104050", "CCCDDD");
		  System.out.println(ac1.toString(IBDCalendar.DATE_FULL));
		  System.out.println(ac1.toString(157));
		  System.out.println("DayOfWeek " + ac1.difference(IBDCalendar.WORKDAYS, ac) );

		  ac = new IBDCalendar("94108-21224", "CCCDDD-HHmmss");
		  System.out.println(ac.toString(IBDCalendar.DATE_FULL));
		  System.out.println(ac.toString(157));
		  System.out.println(Integer.parseInt(ac.toString(159)));
		  ac = new IBDCalendar("104102-121214", "CCCDDD-HHmmss");
		  System.out.println(ac.toString(IBDCalendar.DATE_FULL));
		  System.out.println(ac.toString(157));
		  System.out.println(ac.toString(159));
		}




}