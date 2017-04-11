<%@ page language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/jsp/base.jsp" %>
    <title>宁夏保健学会学习平台-注册</title>
    <script type="text/javascript">
        $(function () {

            makeAlert($("#alertA"));

            $.ajax({
                type: "GET",
                async: false,
                cache: "false",
                url: "framework/get/param.do",
                dataType: "json",
                error: function () {//请求失败时调用函数。
                    showAlert($("#alertA"), "danger");
                },
                success: function (result) {
                    if (result.status == 1 || result.status == 7) {
                        $.each(result.data.forEach(function(value,index,arr) {
                            if(value.name == "title"){
                                $("#title").append("<option value='"+value.value+"'>"+value.value+"</option>");
                            }
                        }));
                    } else {
                        showAlert($("#alertA"), "warning", result.message);
                    }
                }
            });

            $.ajax({
                type: "GET",
                cache: "false",
                url: "framework/get/user.do",
                dataType: "json",
                error: function () {//请求失败时调用函数。
                    showAlert($("#alertA"), "danger");
                },
                success: function (result) {
                    if (result.status == 1) {
                        $("input[name='id']").val(result.data.id);
                        $("input[name='name']").val(result.data.name);
                        $("#sex").val(result.data.sex);
                        $("input[name='identity']").val(result.data.identity);
                        $("input[name='phone']").val(result.data.phone);
                        $("input[name='username']").val(result.data.username);
                        $("input[name='password']").val(result.data.password);
                        $("input[name='password2']").val(result.data.password);
                        $("input[name='unit']").val(result.data.unit);
                        $("input[name='department']").val(result.data.department);
                        $("#title").val(result.data.title);

                    } else {
                        showAlert($("#alertA"), "warning", result.message);
                    }
                }
            });

            $("#update").click(function () {
                if($("input[name='password']").val() != $("input[name='password2']").val()){
                    showAlert($("#alertA"), "warning", "你两次输入的密码不一致，请重新输入！");
                    return;
                }
                $.ajax({
                    type: "POST",
                    cache: "false",
                    url: "framework/update.do",
                    data: $('#userForm').serialize(),
                    dataType: "json",
                    error: function () {//请求失败时调用函数。
                        showAlert($("#alertA"), "danger");
                    },
                    success: function (result) {
                        if (result.status == 1) {
                            showAlert($("#alertA"), "success","修改成功，你可以在本平台进行远程学习！");
                        } else {
                            showAlert($("#alertA"), "warning", result.message);
                        }
                    }
                });
            })

        });

    </script>

</head>

<body>

<div class="row" id="alertA" hidden></div>

<div class="row">
    <div class="col-md-8 col-md-offset-1">
        <div class="panel panel-primary">
            <div class="panel-heading">修改个人信息</div>
            <div class="panel-body">
                <div class="col-md-6 col-md-offset-1">
                    <form role="form" id="userForm">
                        <div class="form-group">
                            <input class="form-control" name="id" type="hidden">
                            <label>姓名：</label>
                            <input class="form-control" type="text" name="name" placeholder="请填写正确的中文名称(支持少数名族，不支持英文、拼音、数字)">
                            <label>性 别：</label>
                            <select id="sex" class="form-control" name="sex">
                            <option value="男">男</option>
                            <option value="女">女</option>
                            </select>
                            <label>身份证号：</label>
                            <input class="form-control" type="text"  name="identity" placeholder="身份证号支持15位、18位两种，支持尾数为X">
                            <label>联系方式：</label>
                            <input class="form-control" type="tel"  name="phone" placeholder="手机号码为11位数字，电话号码为小于等于20位数字，中间可带-，支持区号、分机号">
                            <label>用户名：</label>
                            <input class="form-control" type="text" name="username" placeholder="用户名仅支持字母、数字、下划线，长度为3到16个字符">
                            <label>密 码：</label>
                            <input class="form-control" type="password"  name="password" placeholder="长度为6到22位">
                            <label>确认密码：</label>
                            <input class="form-control" type="password"  name="password2" placeholder="输入一致的密码">
                            <label>单 位：</label>
                            <input class="form-control" type="text" name="unit" placeholder="请输入自己的单位">
                            <label>科 室：</label>
                            <input class="form-control" type="text" name="department" placeholder="请输入自己的科室">
                            <label>职 称：</label>
                            <select id="title" class="form-control" name="title">
                            </select>
                        </div>
                        <a id="update" class="btn btn-primary">修改</a>
                    </form>
                </div>
            </div>
        </div>


    </div><!--/.col-->


</div><!-- /.row -->
</body>

</html>