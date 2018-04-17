function f = calculateGridFeatures(wdata, gridXY, gridYZ, gridZX)
f = zeros(3,3);
% data already multiplied by 10 with 100 added, and only gravity data are
% available in the data matrix
[a, b, c] = featuresSingleGrid( wdata(:, [1,2]), gridXY) ;
f(1,:) = [a, b, c] ;
[a, b, c] = featuresSingleGrid( wdata(:, [2,3]), gridYZ);
f(2,:) = [a, b, c];
[a, b, c] = featuresSingleGrid( wdata(:, [3,1]), gridZX);
f(3,:) = [a, b, c];

f=f(:)';
end

function [zeroCount, weightMean, weightStd] = featuresSingleGrid(d, grid)
m = size(d,1);
w = zeros(m,1);

for i=1:m
    a = d(i,1);
    b = d(i,2);
    w(i,1) = grid(a,b);        
end

zeroCount = sum(w==0);
weightMean = mean(w);
weightStd = std(w);

end