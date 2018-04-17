close all
segs = [];
for sub = 1:34
    segs = [segs; segments(sub).segs];
end
upto = 10;
for i = 1:upto
    segs_e = segs(segs(:, end-1) == i, 5);
    segs_d = segs(segs(:, end) == i, 5);

    figure 
    histogram(segs_e, 0:i*20)
    title(strcat('eating:', num2str(i)))
    figure
    histogram(segs_d, 0:i*20)
    title(strcat('eating:', num2str(i)))
end
