//add strong
$(function () {
  $('#search').autocomplete({
    source: function (request, response) {
      $.ajax({
        url: "/auto-complete/?query=" + document.getElementById("search").value,
        dataType: 'json',
        data: {q: request.term},
        success: function (data) {
          response($.map(data, function (item) {
            return {
              label: __highlight(item.toString(), request.term),
              value: item.toString()
            };
          }));
        }
      });
    },
    minLength: 3/*,
    select: function (event, ui) {
      event.preventDefault();
      var selectedObj = ui.item;
      var content = $('#search').value;
      content = content.replace('<strong>', '').replace('</strong>', '');
      $('#search').innerHTML = content;
    }*/
  })
  .data("ui-autocomplete")._renderItem = function (ul, item) {
    // only change here was to replace .text() with .html()
    return $("<li class=\"ui-menu-item\"></li>")
    .data("ui-autocomplete-item", item)
    .append(
        $("<a ></a>").html(
            item.label))
    .appendTo(ul);
  };

});

function __highlight(s, t) {
  var matcher = new RegExp("(" + $.ui.autocomplete.escapeRegex(t) + ")", "ig");
  return s.replace(matcher, "<strong>" + '$1' + "</strong>");
}

/*$('#search').on('change keyup paste mouseup', function () {
  //alert(this.value);
  var content = $('#search').value;
  content = content.replace('<strong>', '').replace('</strong>', '');
  $('#search').innerHTML = content;
});*/

$(window).on('load', function () {
  $('#myModal').modal('show');
});

function toggle(source) {
  checkboxes = document.getElementsByName('files');
  for(var i=0, n=checkboxes.length;i<n;i++) {
    checkboxes[i].checked = source.checked;
  }
}

$('#indexing').submit(function() {
  checkboxes = document.getElementsByName('files');
  var j = 0;
  for(var i=0, n=checkboxes.length;i<n;i++) {
    var t = checkboxes[i].checked;
    if (t === true) {
      j++;
    }
  }
  if (j === 0) {
    alert('You must one Checked it');
    return false;
  }
  return true; // return false to cancel form action
});