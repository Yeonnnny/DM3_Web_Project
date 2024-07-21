$(document).ready(function () {
    setInitialDates();
    init();
    $('#startDate, #endDate').on('change', updateCharts);
});

function setInitialDates() {
    var currentDate = new Date();
    var firstDayOfMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
    $('#startDate').val(firstDayOfMonth.toISOString().split('T')[0]);
    $('#endDate').val(currentDate.toISOString().split('T')[0]);
}

function init() {
    fetchData();
    updateCharts();
}

function fetchData() {
    // 여러 AJAX 요청을 병렬로 처리
    $.ajax({
        url: "/manager/count",
        method: "get",
        success: function (resp) {
            $("#todayRegisteredProd").html(resp["todayRegisteredProd"]);
            $("#todayWeirdProd").html(resp["todayWeirdProd"]);
            $("#todayRegisteredCustomer").html(resp["todayRegisteredCustomer"]);
            $("#unprocessedReport").html(resp["unprocessedReport"]);
        }
    });

    $.ajax({
        url: "/manager/board/registerProductCount",
        method: "get",
        success: function (data) {
            var createDates = data.map(function (item) {
                return item.createDate;
            });
            var counts = data.map(function (item) {
                return item.count;
            });

            new Chart(document.getElementById("registerProduct"), {
                type: 'line',
                data: {
                    labels: createDates,
                    datasets: [{
                        label: "Total Product Registration",
                        borderColor: 'rgba(54, 162, 235, 1)',
                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                        pointBorderColor: 'rgba(54, 162, 235, 1)',
                        pointBackgroundColor: 'rgba(54, 162, 235, 1)',
                        pointHoverBackgroundColor: '#fff',
                        pointHoverBorderColor: 'rgba(220,220,220,1)',
                        pointBorderWidth: 1,
                        data: counts,
                        lineTension: 0.4
                    }]
                }
            });
        }
    });

    $.ajax({
        url: "/manager/board/categoryProductCount",
        method: "get",
        success: function (data) {
            const labels = Object.keys(data);
            const abnormalData = labels.map(label => data[label]['이상']);
            const normalData = labels.map(label => data[label]['정상']);

            const chartData = {
                labels: labels,
                datasets: [
                    { label: 'Abnormal', data: abnormalData, backgroundColor: 'rgba(255, 99, 132, 0.5)' },
                    { label: 'Normal', data: normalData, backgroundColor: 'rgba(54, 162, 235, 0.5)' }
                ]
            };

            new Chart(document.getElementById('categoryProduct'), {
                type: 'bar',
                data: chartData,
                options: {
                    scales: {
                        x: { stacked: true },
                        y: { stacked: true }
                    }
                }
            });
        }
    });
}

function updateCharts() {
    var startDate = $('#startDate').val();
    var endDate = $('#endDate').val();

    if (!startDate || !endDate) {
        alert("Please select both start and end dates.");
        return;
    }

    $.ajax({
        url: "/manager/board/categoryProductCountByDateRange",
        type: "GET",
        data: { startDate, endDate },
        success: function (response) {
            updateLineChart(response);
            updateDoughnutChart(response);
        },
        error: function (xhr, status, error) {
            console.error("Error fetching chart data:", xhr, status, error);
        }
    });
}

function updateLineChart(data) {
    var dateCounts = data.reduce((acc, item) => {
        var date = item.date.split('T')[0];
        acc[date] = (acc[date] || 0) + item.count;
        return acc;
    }, {});

    var dates = Object.keys(dateCounts).sort();
    var counts = dates.map(date => dateCounts[date]);

    var ctx = document.getElementById('lineChart').getContext('2d');
    if (lineChartInstance) lineChartInstance.destroy();

    lineChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: dates,
            datasets: [{ label: 'Total Product Registration', data: counts, fill: false, borderColor: 'rgba(54, 162, 235, 1)', tension: 0.4 }]
        },
        options: { scales: { y: { beginAtZero: true } }, legend: { display: false } }
    });
}

function updateDoughnutChart(data) {
    var categoryCounts = data.reduce((acc, item) => {
        var category = item.category;
        acc[category] = (acc[category] || 0) + item.count;
        return acc;
    }, {});

    var labels = Object.keys(categoryCounts);
    var chartData = labels.map(label => categoryCounts[label]);

    var ctx = document.getElementById('doughnutChart').getContext('2d');
    if (doughnutChartInstance) doughnutChartInstance.destroy();

    doughnutChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{ label: 'Category Counts', data: chartData, backgroundColor: ['rgba(255, 99, 132, 0.5)', 'rgba(54, 162, 235, 0.5)', 'rgba(255, 206, 86, 0.5)'], borderColor: ['rgba(255, 99, 132, 1)', 'rgba(54, 162, 235, 1)', 'rgba(255, 206, 86, 1)'], borderWidth: 1 }]
        },
        options: { responsive: true, maintainAspectRatio: true, legend: { display: false } }
    });
}