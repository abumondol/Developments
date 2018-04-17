alphas = 1:0.25:4;


st = {'-', '--', '--*', '-.', '-x'};
figure
holdflag = false
for k =1:5    
    fnr = [];
    drate = [];
    for i = 1:length(alphas)
        fnr = [fnr; clusters_loso(k).alpha(i).fnr];
        drate = [drate; clusters_loso(k).alpha(i).drate];
    end
    
    
    plot(alphas, fnr, st{k});
    %plot(alphas, 1-drate, st{k});
    if ~holdflag
        hold on
        holdflag = true
    end
    
    
    
end

xlabel('\alpha', 'FontSize', 14);
lgd = legend('K=1', 'K=2', 'K=3', 'K=4', 'K=5');
legend('Location','southeast')
lgd.FontSize = 14;
title('FNR_1');
