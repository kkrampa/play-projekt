/**
 * Created by kamil on 15.05.14.
 */

function sendAjaxRequest(url, data, type, inserter) {

    $.ajax({
        type: type,
        url: url,
        data: data,
        success: inserter

    });

}