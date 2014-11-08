package com.toinfinityandbeyond.pankaj.careless.dbmodel;

/**
 * Created by pankaj on 6/11/14.
 */
public class CareLessTrip
{
    int id;
    String trpFrm;
    String trpTo;
    String trpDate;
    String time;
    String status;

    //constructors
    public CareLessTrip()
    {

    }

    public CareLessTrip(String frm, String to,String dt)
    {
        this.trpFrm = frm;
        this.trpTo = to;
        this.trpDate = dt;
    }

    //setters
    public void setId(int id)
    {
        this.id=id;
    }

    public void setFrom(String frm)
    {
        this.trpFrm=frm;
    }

    public void setTo(String to)
    {
        this.trpTo=to;
    }

    public void setDate(String dt)
    {
        this.trpDate=dt;
    }

    //getters
    public int getId()
    {
        return this.id;
    }

    public String getFrom()
    {
        return this.trpFrm;
    }

    public String getTo()
    {
        return this.trpTo;
    }

    public String getDate()
    {
        return this.trpDate;
    }
}
