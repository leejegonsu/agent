package pe.kr.ddakker.monitor.agent.transformer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
 
 
/**
 * Example showing how to dynamically insert code using Javassist byte-code editor
 */
public class DynamicCodeInsertion
{
    static String pkgName = "pe.kr.ddakker.monitor.agent.transformer";
    public static void main(String[] args) throws Exception
    {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get(pkgName + ".FooCodeInsertion");
        // Without the call to "makePackage()", package information is lost
        cp.makePackage(cp.getClassLoader(), pkgName);
        CtMethod m = cc.getDeclaredMethod("helloWorld");
        m.insertBefore("{System.out.print(\"Oh, say no to \");}");
        m.insertAfter("{System.out.print(\"And say - Hi World\");}");
        // Changes are not persisted without a call to "toClass()"
        cc.toClass();
 
        (new FooCodeInsertion()).helloWorld();
    }
 
}
 
class FooCodeInsertion
{
    public void helloWorld ()
    {
        System.out.println ("Hello World");
    }
}
