use jni::JNIEnv;
use jni::objects::{JClass, JString, JObject, JValue};
use jni::sys::jobject; // 忘れずに確認
use minacalc_rs::{Calc, RoxCalcExt};
use std::path::PathBuf;

#[no_mangle]
pub extern "system" fn Java_net_mamesosu_api_calculate_CalculateRate_processData<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    input: JString<'local>,
) -> jobject { // 修正1: 戻り値を jstring から jobject に変更

    // null_obj は jobject 型です
    let null_obj = JObject::null().into_raw();

    // JavaのStringをRustのStringに変換
    let input_str: String = env
        .get_string(&input)
        .expect("文字列の取得に失敗しました")
        .into();

    let calc = match Calc::new() {
        Ok(c) => c,
        Err(_) => return null_obj,
    };

    // 修正2: String から PathBuf を作成して渡す
    let path = PathBuf::from(&input_str);

    // PathBufへの参照(&path)を渡す
    let msd_results = match calc.calculate_all_rates_from_file(&path, true) {
        Ok(res) => res,
        Err(_) => return null_obj,
    };

    // 4. 返り値となる Java の HashMap<Double, double[]> を生成
    let map_class = env.find_class("java/util/HashMap").unwrap();
    let result_map = env.new_object(&map_class, "()V", &[]).unwrap();

    let double_class = env.find_class("java/lang/Double").unwrap();

    let rates = [0.7, 1.0, 1.5, 2.0];
    let rate_indices = [0, 3, 8, 13];

    for (rate, &index) in rates.iter().zip(rate_indices.iter()) {
        if index < msd_results.msds.len() {
            let scores = msd_results.msds[index];

            // Key: Double
            let rate_obj = env.new_object(&double_class, "(D)V", &[JValue::Double(*rate)]).unwrap();

            // Value: double[]
            let score_array = env.new_double_array(8).unwrap();
            let score_data = [
                scores.overall as f64,
                scores.stream as f64,
                scores.jumpstream as f64,
                scores.handstream as f64,
                scores.stamina as f64,
                scores.jackspeed as f64,
                scores.chordjack as f64,
                scores.technical as f64
            ];
            env.set_double_array_region(&score_array, 0, &score_data).unwrap();

            // map.put(Double, double[])
            env.call_method(
                &result_map,
                "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                &[JValue::Object(&rate_obj), JValue::Object(&score_array)],
            ).unwrap();
        }
    }

    // jobject として返す
    result_map.into_raw()
}