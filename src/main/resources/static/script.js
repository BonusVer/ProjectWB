$(function(){
    var $registerForm=$("#register");
    var $rstForm=$("#rst");
    var $changeForm=$("#change")

    $changeForm.validate({
        rules:{
            newPass:{
                required:true,
                minlength:6,
                maxlength:12
            },
            cNewPass:{
                required:true,
                equalTo:'#newPass'
            }
        },
        messages:{
            newPass:{
                required:'поле не должно быть пустым',
                minlength:'минимальная длина 6 исмволов',
                maxlength:'максимальная длина 12 символов'
            },
            cNewPass:{
                required:'поле не должно быть пустым',
                equalTo:'пароли не совпадают'
            }
        }
    })

    $rstForm.validate({
        rules:{
            psw:{
                required:true,
                minlength:6,
                maxlength:12
            },
            cpsw:{
                equalTo:'#psw'
            }
        },
        messages:{
            psw:{
                required:'поле не должно быть пустым',
                minlength:'минимальная длина 6 исмволов',
                maxlength:'максимальная длина 12 символов'
            },
            cpsw:{
                equalTo:'пароли не совпадают'
            }
        }
    })

    $registerForm.validate({
        rules:{
            fullName: {
                required:true,
                latter:true,
                minlength:3,
                maxlength:15
            },
            email:{
                required:true,
                email:true
            },
            mobileNumber: {
                required:true,
                minlength:11
            },
            password:{
                required:true,
                minlength:6,
                maxlength:12
            },
            cPassword:{
                required:true,
                equalTo:'#password'
            }
        },
        messages:{
            fullName:{
                required:'поле не должно быть пустым',
                latter:'поле содержит запрещенные символы',
                minlength:'минимальная длина 3 символа',
                maxlength:'максимальная длина 15 символов'
            },
            email:{
                required:'поле не должно быть пустым',
                email: 'e-mail не корректен'
            },
            mobileNumber:{
                required:'поле не должно быть пустым',
                minlength:'введите корректный номер. Пример:89001112233'
            },
            password:{
                required:'поле не должно быть пустым',
                minlength:'минимально 6 символов',
                maxlength:'максимум 12 символов'
            },
            cPassword:{
                required:'поле не должно быть пустым',
                equalTo:'пароли не совпадают'
            }
        }
    })

jQuery.validator.addMethod('latter', function(value, element) {
    return /^[^-\s][a-zA-Zа-яА-ЯЁё0-9_\s-]+$/.test(value);
});

})
