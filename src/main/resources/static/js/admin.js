$(document).ready(function () {

    function getNormalizedData(data) {
        var series = []
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                if (data[key] < 0.5)
                    continue
                console.log(key + " -> " + data[key]);
                var arr = [];
                var number = data[key];
                console.log(number);
                arr.push(key);
                arr.push(parseFloat(number.toFixed(2)));
                series.push(arr)
            }
        }
        return series;
    }

    $("#button-analyze-tone").click(function () {
        $("#graph").empty();
        var sender = $("#sender option:selected").text().trim();
        $.get({
            type: 'get',
            url: '/tone-analyzer-individual',
            dataType: 'json',
            data: 'sender=' + sender,
            success: function (data) {
                console.log(data);
                var series = getNormalizedData(data);
                console.log(series);
                drawDonut(series)
            }
        });
    });

    function drawDonut(dataPoint) {

        Highcharts.chart('graph', {
            chart: {
                type: 'pie',
                options3d: {
                    enabled: true,
                    alpha: 45
                }
            },
            title: {
                text: 'Tone Analyzer'
            },
            subtitle: {
                text: '3D donut'
            },
            plotOptions: {
                pie: {
                    innerSize: 100,
                    depth: 45
                }
            },
            series: [{
                name: 'Tone',
                data: dataPoint
            }]
        });
    }
});
