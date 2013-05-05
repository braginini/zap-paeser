/**
 * Created with IntelliJ IDEA.
 * User: mikhail
 * Date: 13/03/13
 * Time: 09:39
 * To change this template use File | Settings | File Templates.
 */
var csLoad = function() {
    window.setTimeout(function() {
        CS_REC_URL ='http://www.extra.com.br/Site/Chaordic.aspx?page=product&id=' + cs_pid;
        $.ajax({
            url : CS_REC_URL,
            success : function(data) {
                packs = data.split(',');
                try{ csDynamicFrequentlyBought(); } catch(err){};
                try{ csDynamicUltimateBuy(); } catch(err){};
                try{ csDynamicSimilarItems(); } catch(err){};
                try{ csDynamicAlternateBuy(); } catch(err){};
                (_csLoader = window._csLoader || []).push(function(I) {
                    new I.Listener({
                        source : CS_SOURCE
                        ,pack : packs[1]
                        ,fullPack : packs[2]
                    }).visit();
                });
            }
        });
    },1);
}

cs_pid=1834149;
csLoad();
function csChange(pid){
    cs_pid = pid;
};

imgZoom();var __nomeSite = 'Extra.com';var __QtdMaximaProdutosComparacao = '3';var _
_urlCarrinho = 'http://carrinho.extra.com.br';AtualizaItensCookie(null);
function csDynamicFrequentlyBought() {
    window.setTimeout(function() {
        (_csLoader = window._csLoader || []).push(function(I) {
            new I.FrequentlyBought({
                source : CS_SOURCE
                ,pack : packs[0]
            });
        });
    }, 1);
};

function csDynamicUltimateBuy() {
    window.setTimeout(function() {
        (_csLoader = window._csLoader || []).push(function(I) {
            new I.UltimateBuy({
                source : CS_SOURCE
                ,pack : packs[0]
            });
        });
    }, 1);
};

function csDynamicSimilarItems() {
    window.setTimeout(function() {
        (_csLoader = window._csLoader || []).push(function(I) {
            new I.SimilarItems({
                source : CS_SOURCE
                ,pack : packs[0]
            });
        });
    }, 1);
};

