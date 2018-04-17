our = [0.923, 0.764, 0.836];
baseline = [0.772, 0.554, 0.643];

x = [our(1), baseline(1); our(2), baseline(2); our(3), baseline(3) ];
%x = [b1(1), o1(1); b1(2), o1(2); b1(3), o1(3)];
%x = [b2(1), o2(1); b2(2), o2(2); b2(3), o2(3)];
bar(x)
legend('Our Solution', 'Baseline');
Labels = {'Precision','Recall','F1-Score'};
set(gca, 'XTick', 1:3, 'XTickLabel', Labels);

return
o1 = [0.810304449648712, 0.511890166028097, 0.627421824814591];
o2 = [0.921030927835052, 0.68109833971903, 0.780214897963032];

b1 = [0.638418079096045, 0.391816920943135, 0.485603781693167];
b2 = [0.69, 0.47, 0.559];


x = [b1(1), b2(1), o2(1); b1(2), b2(2), o2(2); b1(3), b2(3), o2(3)];
%x = [b1(1), o1(1); b1(2), o1(2); b1(3), o1(3)];
%x = [b2(1), o2(1); b2(2), o2(2); b2(3), o2(3)];
bar(x)
legend('Baseline', 'Extended Baseline', 'Our Solution');
Labels = {'Precision','Recall','F1-Score'};
set(gca, 'XTick', 1:3, 'XTickLabel', Labels);

%x = [b1(1), b2(1), o1(1), o2(1); b1(1), b2(1), o1(1), o2(1); b1(1), b2(1), o1(1), o2(1)];

