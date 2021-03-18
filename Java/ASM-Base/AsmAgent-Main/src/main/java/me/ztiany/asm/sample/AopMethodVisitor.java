package me.ztiany.asm.sample;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AopMethodVisitor extends MethodVisitor implements Opcodes {

    AopMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        this.visitMethodInsn(INVOKESTATIC, "me/ztiany/asm/sample/AopInteceptor", "before", "()V", false);
    }

    @Override
    public void visitInsn(int opcode) {
        /*
            int IRETURN = 172; // visitInsn
            int LRETURN = 173; // -
            int FRETURN = 174; // -
            int DRETURN = 175; // -
            int ARETURN = 176; // -
            int RETURN = 177; // -
        */
        if (opcode >= IRETURN && opcode <= RETURN) {// 在返回之前安插 after 代码。
            this.visitMethodInsn(INVOKESTATIC, "me/ztiany/asm/sample/AopInteceptor", "after", "()V", false);
        }
        super.visitInsn(opcode);
    }

}