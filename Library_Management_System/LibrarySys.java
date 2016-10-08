package librarysys;
import java.io.*;
import java.text.*;
import java.util.Date;
import javax.sound.midi.SysexMessage;

interface Testable
{
    void calcFine(int f);
    void issue(String s);
}
class MyException extends RuntimeException
{
    public String toString()
    {
        return "Invalid rollno.";        
    }
}

class Control 
{
    Date date = new Date();    
    
    public void BStore(Book b[])
    {
        b[0]=new Book("120-234-234-234","Harry Potter","JK Rowling");
        b[1]=new Book("111-23-23-45","Five Point Someone","Chetan Bhagat");
        b[2]=new Book("12-223-34-56","Two States","Chetan Bhagat");   
        
    }
       public void search(Student s[],String n)
    {
        try
        {
        for(int i=0;i<s.length;i++)
        {
           
            if(n.equals(s[i].get_NAME()))
            {
                
                System.out.println("MATCH FOUND..!!\n"+s[i]);
                break;                
            }            
        }
        }
        catch(NullPointerException e)
        {
            System.out.println("NO Match FOUND..!!");
        }
    }
    public void StStore(Staff staff[])
    {
        staff[0]=new Staff("Mr. Ramesh Powar");
        staff[0]=new Staff("Mr. Rohan Kalra");
        staff[2]=new Staff("Mr Rohit Menon");        
    }
    public void SStore(Student s[])
    {
        s[0]=new Student(123,"Amit Mishra","Computer");
        s[1]=new Student(124,"Rahul Singh","IT");
        s[2]=new Student(125,"Rohit Shetty","EnTC");
    }
    public void dispbk(Person s[],Book b[],int cur)
    throws IOException
    {
        try
            {
        for(int i=0;i<3;i++)
        {
            
            if(s[cur].get_BISBN().equals(b[i].get_ISBN()))
            {
               System.out.println("BOOK ISSUED :"+b[i].get_TITLE());                
            }
            }
            
        }
        catch(NullPointerException e)
            {
                System.out.println("No book issued..");
            }
    }
    public void issue(Person s[],Book b[],int cur)
    throws IOException
    {
        int i;
        InputStreamReader input=new InputStreamReader(System.in);
        BufferedReader br=new BufferedReader(input);  
        System.out.println("ENTER BOOK ISBN :");
        String str=br.readLine();  
        try
        {
        for(i=0;i<3;i++)
        {
            if(str.equals(b[i].get_ISBN()))
            {
                s[cur].issue(str);
                b[i].issue(s[cur].get_ID(),new MDate(date.getDate(),date.getMonth()+1,date.getYear()+1900));
                System.out.println("\nBOOK ISSUED SUCCESSFULLY..!!");
                break;
            }            
        }
        if(i==3)
        {
            System.out.println("Invalid ISBN.");
        }
        }
        catch(NullPointerException e)
            {
                System.out.println("Book does not exist..!!");
            }
    } 
    public void retn(Person s[],Book b[],int cur) 
    {
        try
        {
        for(int i=0;i<3;i++)
        {
                
                if(s[cur].retn(b[i].get_ISBN()))
                {
                    int fine=b[i].retn(s[cur].get_ID(),new MDate(date.getDate(),date.getMonth()+1,date.getYear()+1900));
                    s[cur].calcFine(fine);
                    System.out.println("\nBOOK RETURNED SUCCESSFULLY..!!\nBOOK NAME:"+b[i].get_TITLE());
                    break;
                }         
               
                        
        }
        }
        catch(NullPointerException e)
            {
                System.out.println("You have not issued any book yet.");
            }
    }    
}

class Staff extends Person implements Testable 
{        
    public Staff(String NAME)
    {
        this.ID=tid;
        tid=tid+1;
        this.NAME=NAME;
        this.FINE=0;
    }
    public void issue(String BI)
    {
        BISBN=BI;
    }
    public void calcFine(int f)
    {
        FINE=f;
    }
    
    public boolean retn(String BI)
    {
        if(BISBN.equals(BI))
        {
            BISBN="";
            return true;            
        }
        if(BISBN.equals(""))
        {
            return false;
        }
        return false;        
    }
    public String toString()
    {
        return "\nID :"+ID+"\nNAME :"+NAME+"\nFINE : Rs"+FINE;
    }
    public String get_NAME()
    {
        return NAME;
    }
    public String get_BISBN()
    {
        return BISBN;
    }
    public int get_ID()
    {
        return ID;
    }
    public void set_NAME(String n)
    {
        this.NAME=n;
    }
}

class Student extends Person implements Testable
{
    private int ROLLNO;
    private String STREAM;
   
    public Student(int ROLLNO,String NAME,String STREAM)
    {
        this.ID=tid;
        tid=tid+1;
        this.ROLLNO=ROLLNO;
        this.NAME=NAME;
        this.STREAM=STREAM;
        this.FINE=0;        
    }
    public String toString()
    {
        return "ID :"+ID+"\nROLLNO :"+ROLLNO+"\nNAME :"+NAME+"\nSTREAM :"+STREAM+"\nFINE :Rs."+FINE+"/-";
    }
    public void calcFine(int fine)
    {
        FINE=fine;
    }
    public void issue(String BI)
    {
        BISBN=BI;
    }
    public boolean retn(String BI)
    {
        if(BISBN.equals(BI))
        {
            BISBN="";
            return true;            
        }
        if(BISBN.equals(""))
        {
            return false;
        }
        return false;        
    }
    public String get_STREAM()
    {
        return STREAM;
    }
    public int get_ROLLNO()
    {
        return ROLLNO;
    }
    public int get_ID()
    {
        return ID;
    }
    public String get_NAME()
    {
        return NAME;
    }
    public String get_BISBN()
    {
        return BISBN;
    }
    public void set_NAME(String n)
    {
        this.NAME=n;
    }
    public void set_ROLLNO(int r)
    {
        this.ROLLNO=r;
    }
    public void set_STREAM(String str)
    {
        this.STREAM=str;
    }
}


class Person
{
    static int tid;
    protected String NAME;
    protected int ID;
    protected int FINE;
    protected String BISBN;
    static
    {
      tid=100;  
    }
    public void issue(String s)
    {         
    }
    public int get_ID()
    {
        return 100;
    }
    public boolean retn(String BI)
    {
        return false;     
    }
    public void calcFine(int i)
    {
    }
    public String get_BISBN()
    {
        return "";
    }
}


class MDate
{
    private int dd,mm,yy;
    public MDate()
    {
        dd=0;
        mm=0;
        yy=0;
    }
    public MDate(int d,int m,int y)
    {
        dd=d;
        mm=m;
        yy=y;
    }
    public int getDiff(MDate d1)
    {
        return (this.dd-d1.dd);        
    }
    public String toString()
    {
        return dd+"-"+mm+"-"+yy;
    }
}







class Book
{
    private String ISBN,TITLE,AUTHOR;
    boolean ISSUED;
    private MDate IDATE;
    private int ISSUEID;

    public Book(String ISBN,String TITLE,String AUTHOR)
    {
        this.ISBN=ISBN;
        this.TITLE=TITLE;
        this.AUTHOR=AUTHOR;
        this.ISSUED=false;
    }
    public Book()
    {
    }
    public boolean issue(int IID,MDate d)
    {
        if(ISSUED==false)
        {
            ISSUEID=IID;
            ISSUED=true;
            IDATE=d;
            return true;
        }
        return false;       
    }
    public String toString()
    {
        String temp;
        if(this.ISSUED)
            temp="YES";
        else
            temp="NO";
        return "\nISBN :"+ISBN+"\nTITLE :"+TITLE+"\nAUTHOR :"+AUTHOR+"\nISSUED :"+temp;
    }
    public int retn(int IID,MDate d)
    {
        if(IID==ISSUEID)
        {
            ISSUED=false;
            ISSUEID=0;
            System.out.println("ISSUE DATE :"+IDATE+"\nRETURN DATE :"+d);
            return IDATE.getDiff(d);     
           
        }
        return 99;
    }
    public String get_ISBN()
    {
        return ISBN;
    }
    public String get_TITLE()
    {
        return TITLE;
    }
    public String get_AUTHOR()
    {
        return AUTHOR;
    }
    public MDate get_IDATE()
     {
         return IDATE;
     }
     public int get_ISSUEID()
     {
         return ISSUEID;
     }

    public void set_TITLE(String t)
    {
        this.TITLE=t;
    }
    public void set_AUTHOR(String a)
    {
        this.AUTHOR=a;
    }
    public void set_IDATE(MDate m)
     {
         this.IDATE=m;
     }
     public void set_ISSUEID(int i)
     {
         this.ISSUEID=i;
     }
}

public class LibrarySys
{   
    public static void main(String[] args) throws MyException,IOException
    {
        int ch,rno,ch1,temp;
        String name="",str;
        int scount=3,bcount=3,curr=0,stcount=3,stcurr=0,sst=0;
        Control c=new Control();
        Book b[]=new Book[5];
        Student s[]=new Student[5];
        Staff st[]=new Staff[5];
        
        c.BStore(b);
        c.SStore(s);    
        c.StStore(st);
               
        InputStreamReader input=new InputStreamReader(System.in);
        BufferedReader br=new BufferedReader(input);      
        
        System.out.println("***** WELCOME TO LIBRARY MANAGEMNET SYSTEM *****");
        System.out.println("\n1.STUDENT\n2.STAFF\n\nENTER YOUR CHOICE :");
        ch=Integer.parseInt(br.readLine());
        if(ch==1)
        {
            System.out.println("Hello student..!!");
            sst=1;
        }
        else
        {
            System.out.println("Hello staff..!!");
            sst=2;
        }
        curr=scount-1;
        stcurr=stcount-1;
        do
        {
            
         if(sst==1)
         {
            System.out.println("\n***** MENU *****\n1.REGISTER\n2.DISPLAY BOOKS\n3.ISSUE BOOK\n4.RETURN BOOK\n5.CHECK PROFILE\n6.EXIT\n\nENTER YOUR CHOICE :");
            ch1=Integer.parseInt(br.readLine());
         }
         else
         {
             System.out.println("\n***** MENU *****\n1.REGISTER\n2.DISPLAY BOOKS/STUDENT INFO\n3.ISSUE BOOK\n4.RETURN BOOK\n5.CHECK PROFILE\n6.EXIT\n\nENTER YOUR CHOICE :");
             ch1=Integer.parseInt(br.readLine());
         }
        switch(ch1)
        {
            case 1:
                    if(sst==1)
                      {
                        
                        System.out.println("Enter your name :");
                        name=br.readLine();
                        System.out.println("Enter your rollno :");                   
                        rno=Integer.parseInt(br.readLine());   
                        if(rno<0)
                        {
                            throw new MyException();
                        }
                        System.out.println("Enter your stream :");
                        str=br.readLine();
                        s[scount]=new Student(rno,name,str);
                        curr=scount;
                        scount++;
                    }
                    else if(sst==2)
                    {
                        System.out.println("Enter your name :");
                        name=br.readLine();
                        st[stcount]=new Staff(name);
                        stcurr=stcount;
                        stcount++;          
                        
                    }
                    break;
            case 2:
                    if(sst==1)
                    {
                        for(int i=0;i<bcount;i++)
                        {
                            System.out.println(b[i]);
                        }
                    }
                    else
                    {
                        System.out.println("\n1.Book Info\t2.Student Info\nChoice :");
                        int ch2=Integer.parseInt(br.readLine());
                        if(ch2==1)
                        {
                            for(int i=0;i<bcount;i++)
                            {
                                 System.out.println(b[i]);
                            }
                        }
                        else
                        {
                            System.out.println("Enter student name :");
                            String str1=br.readLine();
                            c.search(s,str1);
                        }
                    }
                    break;
            case 3:
                    if(sst==1)
                    {
                        c.issue(s,b,curr);
                    }
                    else if(sst==2)
                    {
                        c.issue(st,b,stcurr);
                    }
                    break;
            case 4:
                   if(sst==1)
                    {
                        c.retn(s,b,curr);
                    }
                    else if(sst==2)
                    {
                        c.retn(st,b,stcurr);
                    }
                    break;
            case 5:
                                        
                    System.out.println("MEMBERSHIP DETAILS :-");
                    if(sst==1)
                    {
                        System.out.println(s[curr]);
                        c.dispbk(s,b,curr);
                    }
                    else
                    {
                        System.out.println(st[stcurr]);   
                        c.dispbk(st,b,stcurr);                        
                    }                    
                    break;                             
        }
        }while(ch1!=6);     
        }     
}
