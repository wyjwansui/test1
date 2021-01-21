package thisTest;

/**
 * Created by meliora on 17/4/9.
 */
public class ThisParent {
    private String name="parent_name";
    public String name1="parent_name1";
    protected String name2="parent_name2";
    public ThisParent(){
        System.out.println("====parent constructor");
        System.out.println(this.name);
        System.out.println(this.name1);
        System.out.println(this.name2);
        //this.print();//如果实例的是子类，调用的就是子类的方法
        //print();//与上一行一样
        test(this);
    }
    public ThisParent(String arg){
        System.out.println("====parent constructor1");
        System.out.println(arg);

    }
    public void print(){
        System.out.println("parent print");
        System.out.println("parent print"+this.name);
        System.out.println("parent print"+this.name1);
        System.out.println("parent print"+this.name2);
    }

    public void test(ThisParent obj){
        System.out.println("====test");
        obj.print();
    }
}
