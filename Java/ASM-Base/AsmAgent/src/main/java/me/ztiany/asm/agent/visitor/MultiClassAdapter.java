package me.ztiany.asm.agent.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 一个转换链并不一定是线性的。可以写一个ClassVisitor去转发所有的函数，也可以使用多个ClassVisitor在同一时间接受调用
 */
public class MultiClassAdapter extends ClassVisitor {

    protected final ClassVisitor[] cvs;

    public MultiClassAdapter(ClassVisitor[] cvs) {
        super(Opcodes.ASM5);
        this.cvs = cvs;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        for (ClassVisitor cv : cvs) {
            cv.visit(version, access, name, signature, superName, interfaces);
        }
    }

}