TPS = [351, 362, 407, 410, 403, 405, 394, 389, 393, 393];
FPS = [ 50,  70, 110,  99,  91, 110,  88,  98, 103, 103];
FNS = [151, 136,  87,  78,  80,  74,  79,  82,  71,  71];

total = 508;

res = [];

for i=1:10       
    params = best_param_res(i).params;    
    
    tp = TPS(i);
    fp = FPS(i);
    fn = total - tp;
    
    p = tp / (tp + fp);
    r = tp / (tp + fn);
    f1 = 2*p*r/(p + r);
    
    res = [res; 100-i, p, r, f1];                    
end

res

figure
x = res(2:end, 1)/100;
y = res(2:end, 4);
plot(x,y);
hold on
grid on
scatter(x,y);
%h_legend=legend('F1-Score','Y','Z');
%set(h_legend,'FontSize',20);

xlabel('Lower bound on recall at segmentation', 'FontSize', 16);
ylabel('Overall accuracy (F1-Score)', 'FontSize', 16);


