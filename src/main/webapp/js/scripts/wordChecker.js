var waitingTime = 3000;
var activityTimeout = setTimeout(inActive, waitingTime);
var checkingNeeded = false;

function inActive() {
	//Send request

	//Color wrong words
	checkingNeeded = false;
}

function resetActive() {
	checkingNeeded = true;
	clearTimeout(activityTimeout);
	activityTimeout = setTimeout(inActive, waitingTime);
}

$(document).bind('keypress', function () {
	resetActive();
});