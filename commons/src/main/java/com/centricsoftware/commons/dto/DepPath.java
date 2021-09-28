package com.centricsoftware.commons.dto;

import cn.hutool.core.collection.ListUtil;
import com.centricsoftware.commons.utils.NodeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Data
public class DepPath {
    private TreeSet<String> paths = Sets.newTreeSet() ;
    private TreeSet<String> urls = Sets.newTreeSet() ;
    private String xml;

    public DepPathResult queryDepPathByUrl(){
        return NodeUtil.queryDepPathByUrl(this);
    }

    public DepPathResult queryDepPathByXML(){
        return NodeUtil.queryDepPathByXML(this);
    }

    public static DepPathBuilder builder(){
        DepPath depPath = new DepPath();
        DepPathBuilder builder = depPath.new DepPathBuilder(depPath);
        return builder;
    }

    public static DepPathXmlBuilder builderXml(){
        DepPath depPath = new DepPath();
        DepPathXmlBuilder builder = depPath.new DepPathXmlBuilder(depPath);
        return builder;
    }

    /**
     * 解析path，比如Child:__Parent__/Child:__Parent__,解析为Child:__Parent__和Child:__Parent__/Child:__Parent__
     * @param paths
     */
    public static List<String> getPathPart(String paths){
        List<String> list = Lists.newArrayList();
        String[] split = paths.split("/");
        for(int i = 0;i<split.length;i++){
            String p = "";
            for(int k=0;k<=i;k++){
                if(k==0){
                    p = split[k];
                }else{
                    p = p +"/"+ split[k];
                }
            }
            list.add(p);
        }
        return list;
    }

    public class DepPathBuilder{
        DepPath depPath;
        DepPathBuilder( DepPath depPath){
            this.depPath = depPath;
        }
        public DepPathBuilder addPath(String path){
            addPaths(path.split(","));
            List<String> pathPart = DepPath.getPathPart(path);
            depPath.getPaths().addAll(pathPart);
            return this;
        }
        public DepPathBuilder addPaths(String[] paths){
            if(paths.length==0){
                return this;
            }
            ArrayList<String> strings = ListUtil.toList(paths);
            strings.forEach(item->{
                List<String> pathPart = DepPath.getPathPart(item);
                depPath.getPaths().addAll(pathPart);
            });
            return this;
        }

        public DepPathBuilder addUrl(String url){
            depPath.getUrls().add(url);
            return this;
        }

        public DepPathBuilder xml(String xml){
            depPath.setXml("<Query>"+xml+"</Query>");
            return this;
        }

        public DepPathBuilder addUrls(String[] urls){
            for(String url:urls){
                depPath.getUrls().add(url);
            }
            return this;
        }
        public DepPathBuilder addUrls(List<String> urls){
            for(String url:urls){
                depPath.getUrls().add(url);
            }
            return this;
        }
        public DepPath build(){
            return  depPath;
        }
    }

    public class DepPathXmlBuilder{
        DepPath depPath;
        DepPathXmlBuilder( DepPath depPath){
            this.depPath = depPath;
        }

        public DepPathXmlBuilder xml(String xml){
            depPath.setXml("<Query>"+xml+"</Query>");
            return this;
        }
        public DepPathXmlBuilder addPath(String path){
            addPaths(path.split(","));
            List<String> pathPart = DepPath.getPathPart(path);
            depPath.getPaths().addAll(pathPart);
            return this;
        }

        public DepPathXmlBuilder addPaths(String[] paths){
            if(paths.length==0){
                return this;
            }
            ArrayList<String> list = ListUtil.toList(paths);
            list.forEach(item->{
                List<String> pathPart = DepPath.getPathPart(item);
                depPath.getPaths().addAll(pathPart);
            });
            return this;
        }

        public DepPathXmlBuilder addUrls(String[] urls){
            for(String url:urls){
                depPath.getUrls().add(url);
            }
            return this;
        }
        public DepPath build(){
            return  depPath;
        }
    }
}