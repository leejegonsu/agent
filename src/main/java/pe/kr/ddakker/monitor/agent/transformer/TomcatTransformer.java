package pe.kr.ddakker.monitor.agent.transformer;
 
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.*;


public class TomcatTransformer implements ClassFileTransformer {
    ClassPool pool =null;
    public TomcatTransformer() {
        this.pool = ClassPool.getDefault();
    }
     
    public byte[] transform(ClassLoader loader, String className, Class redefiningClass, ProtectionDomain domain,
            byte[] bytes)throws IllegalClassFormatException {
//        if (className.contains("StandardEngineValve")) {
//            System.out.println("className: " + className);
//            return transformClass(redefiningClass, bytes);
//        }else if (className.contains("javax/servlet/Filter")) {
//            System.out.println("className: " + className);
//            return transformClass(redefiningClass, bytes);
//        }else {
//            return bytes;
//        }
        byte[] byteCode = bytes;

        // into the transformer will arrive every class loaded so we filter
        // to match only what we need
        if (className.equals("org/apache/catalina/core/StandardEngineValve")) {
            System.out.println("StandardEngineValve");
            try {
                // retrive default Javassist class pool
                ClassPool cp = this.pool;
                System.out.println("StandardEngineValve2");
                // get from the class pool our class with this qualified name
                CtClass cc = cp.get("org.apache.catalina.core.StandardEngineValve");
                // get all the methods of the retrieved class
                CtMethod[] methods = cc.getDeclaredMethods();
                for(CtMethod meth : methods) {
                    // The instrumentation code to be returned and injected
                    final StringBuffer buffer = new StringBuffer();
                    String name = meth.getName();

                    if(name.equals("invoke")){
                        // just print into the buffer a log for example
                        buffer.append("System.out.println(\"Method " + name + " executed\" );");
                        buffer.append("System.out.println(request.getRequestURL().toString());");

                        meth.insertBefore(buffer.toString());
                    }
                }
                // create the byteclode of the class
                byteCode = cc.toBytecode();
                // remove the CtClass from the ClassPool
                cc.detach();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return byteCode;

    }
 
    private byte[] transformClass(Class classToTransform,byte[] b) {
        CtClass cl =null;
        System.out.println("transformClass");
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            System.out.println("classLoader");
            ClassPool pool = this.pool;
            System.out.println("ClassPool");
            pool.insertClassPath(new LoaderClassPath(classLoader));
            System.out.println("insertClassPath");
            System.out.println("1");
            if (pool !=null) {
                System.out.println("2");
                cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
                if (cl.isInterface() ==false) {
                    System.out.println("3");
                    CtBehavior[] methods = cl.getDeclaredBehaviors();
                    for (int i =0; i < methods.length; i++) {
                        System.out.println("4");
                        System.out.println("methods[" + i +"]: " + methods[i]);
                        if (methods[i].isEmpty() ==false) {
                            System.out.println("5");
                            doTransform(methods[i]);
                        }
                    }
                }
                b = cl.toBytecode();
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("e111: " + e);
        }finally {
            if (cl !=null) {
                cl.detach();
            }
        }
        return b;
    }
     
    private void doTransform(CtBehavior method)throws NotFoundException, CannotCompileException {
        System.out.println(method.getName());
        if (method.getName().equals("invoke")) {
             
            System.out.println("0");
            try {
                System.out.println("1");
                method.insertBefore(""
                        +"String jvmRoute = System.getProperty(\"jvmRoute\");"
                        +"String sessionId = $1.getSession().getId();"
                        +"String uri = $1.getRequestURI();"
                        +"pe.kr.ddakker.monitor.websocket.WSClient.send(\"{"
                        +"server: '\" + jvmRoute + \"' "
                        +", sessionId: '\" + sessionId + \"' "
                        +", uri: '\" + uri + \"' "
                        +", stTime: '\" + System.currentTimeMillis() + \"' "
                        +"}\");");
                System.out.println("2");
                method.insertAfter(""
                        +"String jvmRoute = System.getProperty(\"jvmRoute\");"
                        +"String sessionId = $1.getSession().getId();"
                        +"String uri = $1.getRequestURI();"
                        +"pe.kr.ddakker.monitor.websocket.WSClient.send(\"{"
                        +"server: '\" + jvmRoute + \"' "
                        +", sessionId: '\" + sessionId + \"' "
                        +", uri: '\" + uri + \"' "
                        +", edTime: '\" + System.currentTimeMillis() + \"' "
                        +", status: '\" + $2.getStatus() + \"' "
                        +"}\");");
                System.out.println("3");
            }catch (Exception e) {
                System.err.println("Aa e: " + e);
            }
            System.out.println("4");
        }

        if (method.getName().equals("doFilter")) {
            System.out.println("doFilter @@@@@@@@@@@@@@@@@@@@");
        }


    }
 
}
