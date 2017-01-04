(function ($) {
    function query(string) {
        var pairs = string.match(/^\??(.*)$/)[1].split('&');
        return pairs.inject({}, function (params, pairString) {
            var pair = pairString.split('=');
            params[pair[0]] = pair[1];
            return params;
        });
    }

    $(document).ready(function () {
        $('.input-group.date').each(function () {
            $(this).datepicker({
                format: $(this).data('format')
            })
        })
    });
    $(window).load(function () {

        $('.deleteButton').each(function(elem) {
            $(this).click( function(event) {
                if ( !confirm($(this).attr('title') + "?") ) { event.preventDefault() }
            });
        });
        $('input[name=placeholder]').each(function(ix,elem) {
            var next = $(elem).next();
            if (next.is("textarea")) {
                var pre = $('<pre class="pre-scrollable form-control-static placeholder">');
                next.after(pre);
                pre.text($(elem).attr('value'));
                var fill = $('<div class="tool"><a class="btn btn-default"><i class="fa fa-clone"></i></a></div>');
                var fillBtn = fill.find(".btn");
                pre.before(fill);
                fillBtn.click(function(e) {pre.click(); next.val(pre.text());});

                function preShow(isShow) {
                    if (isShow) {
                        next.hide();
                        pre.show();
                        fill.show();
                    } else {
                        pre.hide();
                        fill.hide();
                        next.show();
                    }
                }
                pre.click(function() {next.innerHeight(pre.outerHeight()); next.focus(); preShow(false)});
                preShow(!next.val());

                next.change(function() {
                    preShow(!next.val())
                });
                next.blur(function() {
                    next.change();
                });
            } else {
                next.attr('placeholder', $(elem).attr('value'))
            }
        });
        $('a.HTTP_POST[href], .HTTP_POST a[href]').click(function (event) {
            var elem = this;
            var hr = elem.href;
            var x = hr.indexOf("?");
            if (x > -1) {
                var f = $("<form>");
                f.attr("action", hr.substr(0, x));
                f.attr("method", "post");
                $("body").first().append(f);
                var v = query(hr.substr(x + 1));
                $(v).each(function (x) {
                    var h = $('<input>');
                    h.attr('type', 'hidden');
                    h.attr('name', x.key);
                    h.attr('value', x.value);
                    f.insert(h)
                });
                f.submit()
            }
        })
    })
}(jQuery));
/**
  * Toggle checked flag of checkboxes with name field name, inside same form as clicked-element.
  *
  */
	function toggleChecked(clicked, fieldname) {
		for (var i = 0; i < clicked.form.elements[fieldname].length; i++) {
			clicked.form.elements[fieldname][i].checked = clicked.checked;
		}
	}

	function toggleProjectPermissionsChecked(clicked) {
		var form = clicked.form;
		for (var i = 0; i < form.elements.length; i++) {
			if (form.elements[i].name.indexOf(clicked.name+')') > -1) {
				form.elements[i].checked = clicked.checked;
			}
		}
	}
