package thisTest;

/**
 * Created by meliora on 17/4/9.
 */
public class ThisDemo {
    String name="Mick";
    public void print(String name){
        System.out.println("类中的属性 name="+this.name);
        System.out.println("局部传参的属性="+name);
    }
    public static void main(String[] args) {
        ThisDemo tt=new ThisDemo();
        tt.print("Orson");
    }
}
