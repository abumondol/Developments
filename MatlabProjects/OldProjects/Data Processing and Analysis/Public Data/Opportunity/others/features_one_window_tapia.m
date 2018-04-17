function f = features_one_window_tapia(dlow, dband)        
    DcMean = mean(dlow);
    DCTotalMean = mean(dlow(:));
    DCArea = sum(abs(dlow));
    DCPostureDist =  [DcMean(1)- DcMean(2), DcMean(1)- DcMean(3), DcMean(2)- DcMean(3)];

    dabs = abs(dband);
    ACAbsMean = mean(dabs);
    ACAbsArea = sum(abs);
    ACTotalAbsArea = sum(ACAbsArea);
    %ACTotalSVM = 

end

function res = mcr(d)
    len = size(d,1);
    d = d - repmat(mean(d), len, 1);
    res = mean(abs(diff(sign(d))));
end
