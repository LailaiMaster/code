package me.ztiany.buildsrc

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.pipeline.TransformTask
import com.google.common.collect.Sets
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *@author Ztiany
 *      Email: ztiany3@gmail.com
 *      Date : 2020-05-28 14:25
 */
internal class CustomTransformPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw GradleException("Systrace Plugin, Android Application plugin required")
        }

        val android = project.extensions.getByName("android") as BaseAppModuleExtension
        android.registerTransform(LogTransform())

        project.afterEvaluate {
            android.applicationVariants.all {
                project.gradle.taskGraph.addTaskExecutionGraphListener {
                    //no op
                }
            }
        }
    }

}