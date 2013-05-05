try {
    function callKonvertaZP(src) {
        if (typeof(src) == 'undefined') return;
        var d = document, b = d.body;
        if (src.toUpperCase().indexOf("HTTP://") == 0 || src.toUpperCase().indexOf("HTTPS://") == 0) try {
            var img = d.createElement('IMG');
            var s = src.replace(/%random%/, Math.round(Math.random() * 9999999));
            with (img.style) {
                position = 'absolute';
                width = '0px';
                height = '0px';
            }
            img.src = s;
            b.insertBefore(img, b.firstChild);
        } catch (e) {
        }
    }

    var konverta_url = '';
    try {
        konverta_url = window.location.href;
        konverta_url = encodeURIComponent(konverta_url);
    } catch (e) {
    }
    var konverta_ref = '';
    try {
        konverta_ref = document.referrer == '' ? document.location : document.referrer;
        konverta_ref = encodeURIComponent(konverta_ref);
    } catch (e) {
    }
    callKonvertaZP(('https:' == document.location.protocol ? 'https://' : 'http://pix.') + 'stcounter.com/tag.gif?tag=' + (konverta_tag ? '1' : '0') + '&cid=' + konverta_adcampaign_guid + '&url=' + konverta_url + '&ref=' + konverta_ref + '&rnd=%random%');
    callKonvertaZP(('https:' == document.location.protocol ? 'https://' : 'http://pix.') + 'stcounter.com/tag_g.gif?tag=' + (konverta_tag ? '1' : '0') + '&cid=' + konverta_adcampaign_guid + '&url=' + konverta_url + '&ref=' + konverta_ref + '&rnd=%random%');
    callKonvertaZP(('https:' == document.location.protocol ? 'https://' : 'http://pix.') + 'stcounter.com/tag_k.gif?tag=' + (konverta_tag ? '1' : '0') + '&cid=' + konverta_adcampaign_guid + '&url=' + konverta_url + '&ref=' + konverta_ref + '&rnd=%random%');
} catch (e) {
}
/**
 * Created with IntelliJ IDEA.
 * User: mikhail
 * Date: 11/03/13
 * Time: 18:40
 * To change this template use File | Settings | File Templates.
 */

