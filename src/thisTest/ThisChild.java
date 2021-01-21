package thisTest;

/**
 * Created by meliora on 17/4/9.
 */
public class ThisChild extends ThisParent {
    private String name="child_name";
    public String name1="child_name1";
    protected String name2="child_name2";
    public ThisChild(){
        super();//调用父类无参构造参数,此举可以不写，默认会调
        //super("aaaabbbb");//如果没有指定，默认会调用父类无参构造参数
        System.out.println("====child constructor");
        super.print();
    }

    public static void main(String[] args) {
        ThisChild thisChild = new ThisChild();
        //thisChild.print();
    }
    public void print(){
        System.out.println("child print");
        System.out.println("child print "+this.name);
        System.out.println("child print "+this.name1);
        System.out.println("child print "+this.name2);
    }
}
